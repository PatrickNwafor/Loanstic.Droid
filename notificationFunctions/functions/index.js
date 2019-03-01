'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendGroupPendingApprovalNotification = functions.firestore.document('/notification/{loan_officer_id}/group_notification/{notification_id}').onCreate((change, context) => {
    const notification_id = context.params.notification_id;
    const loan_officer_id = context.params.loan_officer_id;

    //getting notification details
    return admin.firestore().collection('notification').doc(loan_officer_id).collection('group_notification').doc(notification_id).get()
    .then(result => {
        
        const groupId = result.data().groupId;

        return admin.firestore().collection('Account').doc(loan_officer_id).get()
        .then(userdata => {
            
            const firstName = userdata.data().firstName;
            const lastName = userdata.data().lastName;

            return admin.firestore().collection('Borrowers_Group').doc(groupId).get()
            .then(group => {
                
                const groupName = group.data().groupName;

                return admin.firestore().collection('Account').where('accountType', '==', 'branch_manager').get()
                .then(snapshot  =>{
                    if (snapshot.empty) {
                        console.log('No matching documents.');
                        return;
                    }  
                
                    snapshot.forEach(doc => {
                        console.log(doc.id, '=>', doc.data());

                        const data = doc.data();

                        const payload = {
                            notification: {
                              title : "Pending Group Approval",
                              body: firstName+" "+lastName+" has created a new group called "+groupName,
                              icon: "default",
                              clickAction: "com.icubed.loansticdroid_TARGET_NOTIFICATION"
                            },
                            data : {
                              groupId : groupId,
                              loan_officer_id : loan_officer_id,
                              group_notification : "group_notification"
                            }
                          };
                      
                          return admin.messaging().sendToDevice(data.device_token, payload).then(response => {
                      
                              console.log('This was the notification feature');
                              return true;
                      
                          })
                          .catch(error => {
                            //handle error
                            console.log(error);
                            
                          });
                    });

                    return true;
                })
                .catch(error => {
                    //handle error
                    console.log(error);
                    
                });

            })
            .catch(error => {
                //handle error
                console.log(error);
                
            });

        })
        .catch(error => {
            //handle error
            console.log(error);
            
        });

    })
    .catch(error => {
        //handle error
        console.log(error);
        
    });
});

exports.sendBorrowerPendingApprovalNotification = functions.firestore.document('/notification/{loan_officer_id}/borrower_pending_approval/{notification_id}').onCreate((change, context) => {
    const notification_id = context.params.notification_id;
    const loan_officer_id = context.params.loan_officer_id;

    //getting notification details
    return admin.firestore().collection('notification').doc(loan_officer_id).collection('borrower_pending_approval').doc(notification_id).get()
    .then(result => {
        const borrowerId = result.data().borrowerId;

        return admin.firestore().collection('Borrowers').doc(borrowerId).get()
        .then(borrower => {

            const firstName = borrower.data().firstName;
            const lastName = borrower.data().lastName;

            return admin.firestore().collection('Account').where('accountType', '==', 'branch_manager').get()
                .then(snapshot  =>{
                    if (snapshot.empty) {
                        console.log('No matching documents.');
                        return;
                    }  
                
                    snapshot.forEach(doc => {
                        console.log(doc.id, '=>', doc.data());

                        const data = doc.data();

                        const payload = {
                            notification: {
                              title : "Pending Borrower Approval",
                              body: "Borrower "+firstName+" "+lastName+" is awaiting approval",
                              icon: "default",
                              clickAction: "com.icubed.loansticdroid_PENDING_BORROWER_APPROVAL"
                            },
                            data : {
                              borrowerId : borrowerId,
                              loan_officer_id : loan_officer_id,
                              borrower_pending_approval : "borrower_pending_approval"
                            }
                          };
                      
                          return admin.messaging().sendToDevice(data.device_token, payload).then(response => {
                      
                              console.log('This was the notification feature');
                              return true;
                      
                          })
                          .catch(error => {
                            //handle error
                            console.log(error);
                            
                          });
                    });

                    return true;
                })
                .catch(error => {
                    //handle error
                    console.log(error);
                    
                });
        })
    })
});

exports.sendLoanRequestNotification = functions.firestore.document('/notification/{loan_officer_id}/loan_request_notification/{notification_id}').onCreate((change, context) => {
    const notification_id = context.params.notification_id;
    const loan_officer_id = context.params.loan_officer_id;

    //getting notification details
    return admin.firestore().collection('notification').doc(loan_officer_id).collection('loan_request_notification').doc(notification_id).get()
    .then(result => {
        const loanId = result.data().loanId;

        return admin.firestore().collection('Loan').doc(loanId).get()
        .then(borrower => {

            const loanTypeId = borrower.data().loanTypeId;

            return admin.firestore().collection('Loan_Type').doc(loanTypeId).get()
            .then(loanType => {

                const loanTypeName = loanType.data().loanTypeName;

                return admin.firestore().collection('Account').where('accountType', '==', 'branch_manager').get()
                .then(snapshot  =>{
                    if (snapshot.empty) {
                        console.log('No matching documents.');
                        return;
                    }  
                
                    snapshot.forEach(doc => {
                        console.log(doc.id, '=>', doc.data());

                        const data = doc.data();

                        const payload = {
                            notification: {
                              title : "Loan Request",
                              body: "A new loan request of type "+loanTypeName+" is awaiting approval",
                              icon: "default",
                              clickAction: "com.icubed.loansticdroid_PENDING_LOAN_REQUEST"
                            },
                            data : {
                              loanId : loanId,
                              pending_loan_approval : "pending_loan_approval"
                            }
                          };
                      
                          return admin.messaging().sendToDevice(data.device_token, payload).then(response => {
                      
                              console.log('This was the notification feature');
                              return true;
                      
                          })
                          .catch(error => {
                            //handle error
                            console.log(error);
                            
                          });
                    });

                    return true;
                })
                .catch(error => {
                    //handle error
                    console.log(error);
                    
                });
            })
            .catch(error => {
                //handle error
                console.log(error);
                
            });
        })
    })
});
