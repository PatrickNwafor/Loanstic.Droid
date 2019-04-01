package com.icubed.loansticdroid.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanCollectionQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SavingsScheduleGenerator {
    SavingsTable savings;
    private SavingsPlanCollectionQueries savingsPlanCollectionQueries;
    private SavingsQueries savingsQueries;

    public static final String COLLECTION_STATE_FULL = "Full Collection";
    public static final String COLLECTION_STATE_PARTIAL = "Partial Collection";
    public static final String COLLECTION_STATE_NO = "No Collection";

    private double totalPeriodicAmount = 0;

    public SavingsScheduleGenerator() {
        savingsPlanCollectionQueries = new SavingsPlanCollectionQueries();
        savingsQueries = new SavingsQueries();
    }

    public void generateMoneyTargetSchedule(SavingsTable savingsTable){
        savings = savingsTable;

        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = generateCollection(generateInterest());
        Log.d(TAG, "generateRepaymentSchedule: "+savingsPlanCollectionTable.toString());

        sendCollectionToCloud(savingsPlanCollectionTable);
    }

    public void generateTimeTargetSchedule(SavingsTable savingsTable){
        savings = savingsTable;

        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = generateCollectionTime();
        Log.d(TAG, "generateRepaymentSchedule: "+savingsPlanCollectionTable.toString());

        sendCollectionToCloud(savingsPlanCollectionTable);
        updateSavingsDetails();
    }

    private void updateSavingsDetails() {

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("lastUpdated", new Date());
        objectMap.put("totalExpectedPeriodicAmount", totalPeriodicAmount);

        savingsQueries.updateSavingsDetails(objectMap, savings.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: savings updated");
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });

    }

    private void sendCollectionToCloud(List<SavingsPlanCollectionTable> savingsPlanCollectionTable) {
        for (SavingsPlanCollectionTable collectionTable : savingsPlanCollectionTable) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("savingsCollectionNumber", collectionTable.getSavingsCollectionNumber());
            objectMap.put("savingsId", collectionTable.getSavingsId());
            objectMap.put("savingsCollectionAmount", collectionTable.getSavingsCollectionAmount());
            objectMap.put("savingsCollectionDueDate", collectionTable.getSavingsCollectionDueDate());
            objectMap.put("lastUpdatedAt", collectionTable.getLastUpdatedAt());
            objectMap.put("timestamp", collectionTable.getTimestamp());
            objectMap.put("isSavingsCollected", collectionTable.getIsSavingsCollected());
            objectMap.put("collectionState", collectionTable.getSavingsCollectionState());
            objectMap.put("amountPaid", 0.0);

            savingsPlanCollectionQueries.createSavingsScheduleCollection(objectMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: collection created");
                            }else{
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private List<SavingsPlanCollectionTable> generateCollection(double repaymentAmount){

        switch (savings.getSavingsDurationUnit()){
            case DateUtil.PER_DAY:
                return generateRepaymentPerDay(repaymentAmount);

            case DateUtil.PER_MONTH:
                return generateRepaymentPerMonth(repaymentAmount);

            case DateUtil.PER_WEEK:
                return generateRepaymentPerWeek(repaymentAmount);

            default:
                return null;
        }
    }

    private List<SavingsPlanCollectionTable> generateCollectionTime(){

        switch (savings.getSavingsDurationUnit()){
            case DateUtil.PER_DAY:
                return generateRepaymentPerDay();

            case DateUtil.PER_MONTH:
                return generateRepaymentPerMonth();

            case DateUtil.PER_WEEK:
                return generateRepaymentPerWeek();

            default:
                return null;
        }
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerWeek() {
        Calendar now = Calendar.getInstance();
        now.setTime(savings.getStartDate());

        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();
        int count = 1;

        while (now.getTime().before(savings.getMaturityDate())){

            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(now.getTime());
            collectionTable.setIsSavingsCollected(false);
            collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(count);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);
            totalPeriodicAmount = totalPeriodicAmount + savings.getDepositAmount();
            now.set(Calendar.MONTH, now.get(Calendar.MONTH) + savings.getSavingsDuration());
            count++;
        }

        return savingsPlanCollectionTable;
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerMonth() {
        Calendar now = Calendar.getInstance();
        now.setTime(savings.getStartDate());

        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();
        int count = 1;

        while (now.getTime().before(savings.getMaturityDate())){

            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(now.getTime());
            collectionTable.setIsSavingsCollected(false);
            collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(count);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);
            totalPeriodicAmount = totalPeriodicAmount + savings.getDepositAmount();
            now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + savings.getSavingsDuration());
            count++;
        }

        return savingsPlanCollectionTable;
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerDay() {

        Calendar now = Calendar.getInstance();
        now.setTime(savings.getStartDate());

        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();
        int count = 1;

        while (now.getTime().before(savings.getMaturityDate())){

            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(now.getTime());
            collectionTable.setIsSavingsCollected(false);
            collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(count);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);
            totalPeriodicAmount = totalPeriodicAmount + savings.getDepositAmount();
            now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + savings.getSavingsDuration());
            count++;
        }

        return savingsPlanCollectionTable;
    }

    private double decimalFormat(double d){
        DecimalFormat df = new DecimalFormat("#.###");
        return Double.parseDouble(df.format(d));
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerWeek(double repaymentAmount) {
        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();

        int numberOfRepaymentDays = (int) Math.ceil(repaymentAmount/savings.getDepositAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentDays-1) * savings.getDepositAmount());
        lastRepaymentAmount = decimalFormat(lastRepaymentAmount);

        Log.d(TAG, "lastRepaymentAmount: "+lastRepaymentAmount);

        int weeksCount = savings.getSavingsDuration();

        for(int i = 1; i <= numberOfRepaymentDays; i++){
            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(DateUtil.addDay(savings.getStartDate(), weeksCount*7));
            collectionTable.setIsSavingsCollected(false);
            if(i==numberOfRepaymentDays) collectionTable.setSavingsCollectionAmount(lastRepaymentAmount);
            else collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(i);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);

            weeksCount = weeksCount + savings.getSavingsDuration();
        }

        return savingsPlanCollectionTable;
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerMonth(double repaymentAmount) {
        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();

        int numberOfRepaymentDays = (int) Math.ceil(repaymentAmount / savings.getDepositAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentDays - 1) * savings.getDepositAmount());
        lastRepaymentAmount = decimalFormat(lastRepaymentAmount);

        Log.d(TAG, "lastRepaymentAmount: " + lastRepaymentAmount);

        int monthCount = savings.getSavingsDuration();

        for (int i = 1; i <= numberOfRepaymentDays; i++) {
            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(DateUtil.addMonth(savings.getStartDate(), monthCount));
            collectionTable.setIsSavingsCollected(false);
            if (i == numberOfRepaymentDays) collectionTable.setSavingsCollectionAmount(lastRepaymentAmount);
            else collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(i);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);

            monthCount = monthCount + savings.getSavingsDuration();
        }

        return savingsPlanCollectionTable;
    }

    private double generateInterest(){
        return savings.getAmountTarget();
    }

    private List<SavingsPlanCollectionTable> generateRepaymentPerDay(double repaymentAmount) {
        List<SavingsPlanCollectionTable> savingsPlanCollectionTable = new ArrayList<>();

        int numberOfRepaymentDays = (int) Math.ceil(repaymentAmount/savings.getDepositAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentDays-1) * savings.getDepositAmount());
        lastRepaymentAmount = decimalFormat(lastRepaymentAmount);

        Log.d(TAG, "lastRepaymentAmount: "+lastRepaymentAmount);

        int daysCount = savings.getSavingsDuration();

        for(int i = 1; i <= numberOfRepaymentDays; i++){
            SavingsPlanCollectionTable collectionTable = new SavingsPlanCollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setSavingsCollectionDueDate(DateUtil.addDay(savings.getStartDate(), daysCount));
            collectionTable.setIsSavingsCollected(false);
            if(i==numberOfRepaymentDays) collectionTable.setSavingsCollectionAmount(lastRepaymentAmount);
            else collectionTable.setSavingsCollectionAmount(savings.getDepositAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setSavingsCollectionNumber(i);
            collectionTable.setSavingsId(savings.getSavingsId());
            collectionTable.setAmountPaid(0.0);
            collectionTable.setSavingsCollectionState(COLLECTION_STATE_NO);

            savingsPlanCollectionTable.add(collectionTable);

            daysCount = daysCount + savings.getSavingsDuration();
        }

        return savingsPlanCollectionTable;
    }
}
