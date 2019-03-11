package com.icubed.loansticdroid.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PaymentScheduleGenerator {

    LoansTable loan;
    private CollectionQueries collectionQueries;

    public static final String COLLECTION_STATE_FULL = "Full Collection";
    public static final String COLLECTION_STATE_PARTIAL = "Partial Collection";
    public static final String COLLECTION_STATE_NO = "No Collection";

    public PaymentScheduleGenerator() {
        collectionQueries = new CollectionQueries();
    }

    public void generateRepaymentSchedule(LoansTable loansTable){
        loan = loansTable;

        List<CollectionTable> collectionTableList = generateCollection(calculateFullRepaymentAmount());
        Log.d(TAG, "generateRepaymentSchedule: "+collectionTableList.get(collectionTableList.size() - 1));

        sendCollectionToCloud(collectionTableList);
    }

    private void sendCollectionToCloud(List<CollectionTable> collectionTableList) {
        for (CollectionTable collectionTable : collectionTableList) {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("collectionNumber", collectionTable.getCollectionNumber());
            objectMap.put("loanId", collectionTable.getLoanId());
            objectMap.put("collectionDueAmount", collectionTable.getCollectionDueAmount());
            objectMap.put("collectionDueDate", collectionTable.getCollectionDueDate());
            objectMap.put("lastUpdatedAt", collectionTable.getLastUpdatedAt());
            objectMap.put("timestamp", collectionTable.getTimestamp());
            objectMap.put("isDueCollected", collectionTable.getIsDueCollected());
            objectMap.put("penalty", collectionTable.getPenalty());
            objectMap.put("collectionState", collectionTable.getCollectionState());
            objectMap.put("amountPaid", 0);

            collectionQueries.createCollection(objectMap)
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

    private double calculateFullRepaymentAmount(){
        double interestRate = loan.getLoanInterestRate();
        double principal = loan.getLoanAmount();

        Log.d(TAG, "calculateFullRepaymentAmount: "+((interestRate+100)/100) * principal);
        return decimalFormat((((interestRate+100)/100) * principal));
    }

    private List<CollectionTable> generateCollection(double repaymentAmount){

        switch (loan.getRepaymentAmountUnit()){
            case DateUtil.PER_DAY:
                return generateRepaymentPerDay(repaymentAmount);

            case DateUtil.PER_MONTH:
                return generateRepaymentPerMonth(repaymentAmount);

            case DateUtil.PER_WEEK:
                return generateRepaymentPerWeek(repaymentAmount);

            case DateUtil.PER_YEAR:
                return generateRepaymentPerYear(repaymentAmount);

                default:
                    return null;
        }
    }

    private double decimalFormat(double d){
        DecimalFormat df = new DecimalFormat("#.###");
        return Double.parseDouble(df.format(d));
    }

    private List<CollectionTable> generateRepaymentPerWeek(double repaymentAmount) {
        List<CollectionTable> collectionTableList = new ArrayList<>();

        int numberOfRepaymentWeek = (int) Math.ceil(repaymentAmount/loan.getRepaymentAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentWeek-1) * loan.getRepaymentAmount());
        lastRepaymentAmount = decimalFormat(lastRepaymentAmount);

        for(int i = 1; i <= numberOfRepaymentWeek; i++){
            CollectionTable collectionTable = new CollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setCollectionDueDate(DateUtil.addDay(loan.getLoanCreationDate(), 7*i));
            collectionTable.setIsDueCollected(false);
            if(i==numberOfRepaymentWeek) collectionTable.setCollectionDueAmount(lastRepaymentAmount);
            else collectionTable.setCollectionDueAmount(loan.getRepaymentAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setCollectionNumber(i);
            collectionTable.setLoanId(loan.getLoanId());
            collectionTable.setPenalty(0.0);
            collectionTable.setAmountPaid(0.0);
            collectionTable.setCollectionState(COLLECTION_STATE_NO);

            collectionTableList.add(collectionTable);
        }

        return collectionTableList;
    }

    private List<CollectionTable> generateRepaymentPerMonth(double repaymentAmount) {
        List<CollectionTable> collectionTableList = new ArrayList<>();

        int numberOfRepaymentMonths = (int) Math.ceil(repaymentAmount/loan.getRepaymentAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentMonths-1) * loan.getRepaymentAmount());

        for(int i = 1; i <= numberOfRepaymentMonths; i++){
            CollectionTable collectionTable = new CollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setCollectionDueDate(DateUtil.addMonth(loan.getLoanCreationDate(), i));
            collectionTable.setIsDueCollected(false);
            if(i==numberOfRepaymentMonths) collectionTable.setCollectionDueAmount(lastRepaymentAmount);
            else collectionTable.setCollectionDueAmount(loan.getRepaymentAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setCollectionNumber(i);
            collectionTable.setLoanId(loan.getLoanId());
            collectionTable.setPenalty(0.0);
            collectionTable.setAmountPaid(0.0);
            collectionTable.setCollectionState(COLLECTION_STATE_NO);

            collectionTableList.add(collectionTable);
        }

        return collectionTableList;
    }

    private List<CollectionTable> generateRepaymentPerYear(double repaymentAmount) {
        List<CollectionTable> collectionTableList = new ArrayList<>();

        int numberOfRepaymentYears = (int) Math.ceil(repaymentAmount/loan.getRepaymentAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentYears-1) * loan.getRepaymentAmount());

        for(int i = 1; i <= numberOfRepaymentYears; i++){
            CollectionTable collectionTable = new CollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setCollectionDueDate(DateUtil.addYear(loan.getLoanCreationDate(), i));
            collectionTable.setIsDueCollected(false);
            if(i==numberOfRepaymentYears) collectionTable.setCollectionDueAmount(lastRepaymentAmount);
            else collectionTable.setCollectionDueAmount(loan.getRepaymentAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setCollectionNumber(i);
            collectionTable.setLoanId(loan.getLoanId());
            collectionTable.setPenalty(0.0);
            collectionTable.setAmountPaid(0.0);
            collectionTable.setCollectionState(COLLECTION_STATE_NO);

            collectionTableList.add(collectionTable);
        }

        return collectionTableList;
    }

    private List<CollectionTable> generateRepaymentPerDay(double repaymentAmount) {
        List<CollectionTable> collectionTableList = new ArrayList<>();

        int numberOfRepaymentDays = (int) Math.ceil(repaymentAmount/loan.getRepaymentAmount());
        double lastRepaymentAmount = repaymentAmount - ((numberOfRepaymentDays-1) * loan.getRepaymentAmount());

        Log.d(TAG, "lastRepaymentAmount: "+lastRepaymentAmount);

        for(int i = 1; i <= numberOfRepaymentDays; i++){
            CollectionTable collectionTable = new CollectionTable();
            collectionTable.setTimestamp(new Date());
            collectionTable.setCollectionDueDate(DateUtil.addDay(loan.getLoanCreationDate(), i));
            collectionTable.setIsDueCollected(false);
            if(i==numberOfRepaymentDays) collectionTable.setCollectionDueAmount(lastRepaymentAmount);
            else collectionTable.setCollectionDueAmount(loan.getRepaymentAmount());
            collectionTable.setLastUpdatedAt(new Date());
            collectionTable.setCollectionNumber(i);
            collectionTable.setLoanId(loan.getLoanId());
            collectionTable.setPenalty(0.0);
            collectionTable.setAmountPaid(0.0);
            collectionTable.setCollectionState(COLLECTION_STATE_NO);

            collectionTableList.add(collectionTable);
        }

        return collectionTableList;
    }
}
