
var admin = require("firebase-admin");

var serviceAccount = require("loanstic-firebase-adminsdk-38mis-c3a7df73d5.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://loanstic.firebaseio.com"
});
