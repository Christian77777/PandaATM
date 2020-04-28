package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import data_access_atm.ATMDA;
import data_access_atm.AccountDA;
import data_access_atm.AccountOpeningDA;
import data_access_atm.BankBranchDA;
import data_access_atm.CardActivationDA;
import data_access_atm.ClientDA;
import data_access_atm.DebitCardDA;
import entity_atm.Client;

public class DataInitialization {

	private BankBranchDA bankBranchData;
	private ClientDA clientData;
	private AccountDA accountData;
	private AccountOpeningDA accountOpeningData;
	private DebitCardDA debitCardData;
	private CardActivationDA cardActivationData;
	private ATMDA atmData;
	
	private DataInitialization(Database db) throws SQLException {
		//Erases all data in Database
		Statement stmt = db.getDatabase().createStatement();
		stmt.executeUpdate("DELETE FROM `BankBranch`");
		stmt.executeUpdate("DELETE FROM `Client`");
		stmt.executeUpdate("DELETE FROM `Account`");
		stmt.executeUpdate("DELETE FROM `AccountOpening`");
		stmt.executeUpdate("DELETE FROM `ATM`");
		stmt.executeUpdate("DELETE FROM `ATMSession`");
		stmt.executeUpdate("DELETE FROM `DebitCard`");
		stmt.executeUpdate("DELETE FROM `Transaction`");
		stmt.executeUpdate("DELETE FROM `CardActivation`");
		stmt.close();
		
		//Initialize all Data Access Classes
		bankBranchData = new BankBranchDA(db);
		clientData = new ClientDA(db);
		accountData = new AccountDA(db);
		accountOpeningData = new AccountOpeningDA(db);
		debitCardData = new DebitCardDA(db);
		cardActivationData = new CardActivationDA(db);
		atmData = new ATMDA(db);
		
		//Initialize Bank Branch (APP ONLY HAS 1 BRANCH)
		int branchOne = bankBranchData.insertBankBranch("3801 W Temple Ave, Pomona, CA 91768");
		
		//Initialize ATM's (APP HAS EXACTLY 5 UNIQUE ATM LOCATIONS)
		atmData.insertATM("3801 W Temple Ave, Pomona, CA 91768 - Bldg. 1", branchOne);
		atmData.insertATM("3801 W Temple Ave, Pomona, CA 91768 - Bldg. 17", branchOne);
		atmData.insertATM("3801 W Temple Ave, Pomona, CA 91768 - Marketplace", branchOne);
		atmData.insertATM("3801 W Temple Ave, Pomona, CA 91768 - Bronco Student Center", branchOne);
		atmData.insertATM("3801 W Temple Ave, Pomona, CA 91768 - Library", branchOne);
		
		//Initialize Clients - There are 5 (Kenny Lee, Ashley Yu, Christian Devile, Nicholas Stewart, Jonathan Halim) 
		int clientKenny = clientData.insertClient("Kenny Lee", "123 Main St.", "909-696-6969", LocalDateTime.of(1999, 10, 1, 12, 30), branchOne);
		int clientAshley = clientData.insertClient("Ashley Yu", "345 1st St.", "909-696-6420", LocalDateTime.of(2000, 11, 28, 12, 30), branchOne);
		int clientChristian = clientData.insertClient("Christian Devile", "456 Valley View", "606-222-0420", LocalDateTime.of(1999, 2, 10, 12, 30), branchOne);
		int clientNick = clientData.insertClient("Nicholas Stewart", "567 One Infinite Loop", "111-111-1111", LocalDateTime.of(1999, 3, 24, 12, 30), branchOne);
		int clientJonathan = clientData.insertClient("Jonathan Halim", "789 Ashbury Pkwy.", "545-949-8100", LocalDateTime.of(2000, 7, 30, 12, 30), branchOne);
		
		//Get Info For each Client (Needed for Debit Card Creation)
		Client clientKennyInfo = clientData.getClientInfo(clientKenny);
		Client clientAshleyInfo = clientData.getClientInfo(clientAshley);
		Client clientChristianInfo = clientData.getClientInfo(clientChristian);
		Client clientNickInfo = clientData.getClientInfo(clientNick);
		Client clientJonathanInfo = clientData.getClientInfo(clientJonathan);
		
		//Initiate Accounts - # of Accounts Varies from User to User
		int kennyChecking = accountData.insertCheckingAcc("Kenny's Checking SPENDY BOI");
		int ashleyChecking = accountData.insertCheckingAcc("Ashley's Checking");
		int ashleySavings = accountData.insertSavingsAcc("Ashley's Savings");
		int christianChecking = accountData.insertCheckingAcc("Christian's Checking");
		int christianSavingsOne = accountData.insertSavingsAcc("Christian's Savings");
		int christianSavingsTwo = accountData.insertSavingsAcc("Christian's College Fund");
		int nickChecking = accountData.insertCheckingAcc("Nicholas's Checking");
		int nickSavings = accountData.insertSavingsAcc("Nicholas's Savings");
		int jonathanSavings = accountData.insertSavingsAcc("Jonathan's V-Bucks Allowance");
		
		//Initiate Account Openings - 1 for each Account
		accountOpeningData.insertAccountOpening(clientKenny, kennyChecking);
		accountOpeningData.insertAccountOpening(clientAshley, ashleyChecking);
		accountOpeningData.insertAccountOpening(clientAshley, ashleySavings);
		accountOpeningData.insertAccountOpening(clientChristian, christianChecking);
		accountOpeningData.insertAccountOpening(clientChristian, christianSavingsOne);
		accountOpeningData.insertAccountOpening(clientChristian, christianSavingsTwo);
		accountOpeningData.insertAccountOpening(clientNick, nickChecking);
		accountOpeningData.insertAccountOpening(clientNick, nickSavings);
		accountOpeningData.insertAccountOpening(clientJonathan, jonathanSavings);
		
		//Initiate Debit Cards - 1 for each Client
		long kennyCard = debitCardData.insertDebitCard(clientKennyInfo.getCustomerName() ,LocalDateTime.now().plusYears(4L), 1234, clientKenny, branchOne);
		long ashleyCard = debitCardData.insertDebitCard(clientAshleyInfo.getCustomerName() ,LocalDateTime.now().plusYears(2L), 1234, clientAshley, branchOne);
		long christianCard = debitCardData.insertDebitCard(clientChristianInfo.getCustomerName() ,LocalDateTime.now().plusYears(5L), 1234, clientChristian, branchOne);
		long nickCard = debitCardData.insertDebitCard(clientNickInfo.getCustomerName() ,LocalDateTime.now().plusYears(3L), 1234, clientNick, branchOne);
		long jonathanCard = debitCardData.insertDebitCard(clientJonathanInfo.getCustomerName() ,LocalDateTime.now(), 1234, clientJonathan, branchOne);
		
		//Initiate Card Activations - (Each Card May be linked to multiple accounts)
		cardActivationData.insertCardActivation(kennyCard, kennyChecking);
		cardActivationData.insertCardActivation(ashleyCard, ashleyChecking);
		cardActivationData.insertCardActivation(ashleyCard, ashleySavings);
		cardActivationData.insertCardActivation(christianCard, christianChecking);
		cardActivationData.insertCardActivation(christianCard, christianSavingsOne);
		cardActivationData.insertCardActivation(christianCard, christianSavingsTwo);
		cardActivationData.insertCardActivation(nickCard, nickChecking);
		cardActivationData.insertCardActivation(nickCard, nickSavings);
		cardActivationData.insertCardActivation(jonathanCard, jonathanSavings);
	}//end Constructor
	
	public static void main(String[] args) throws SQLException {
		Database db = new Database();
		new DataInitialization(db);
		System.out.println("Data Initialization Successful");
	}//end main
}//end DataInitialization