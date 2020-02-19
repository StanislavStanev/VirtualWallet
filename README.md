# VirtualWallet
Web application that lets users make transactions between each other similar to PayPal and Skrill.

## Team Members
* Stanislav Stanev - [GitHub](https://github.com/StanislavStanev)
* Radi Arnaudov - [GitHub]

## Project Description
### Areas
* **User part** - available for registered users only
* **Administrative part** - available for administrators only

#### User Part

* After login, users see everything visible to website visitors and additionally they can:
    
     ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581951649/login_zuhpys.jpg)
     
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581951649/home_amzsqf.png)
     
     * Add a Bank Card
     * Make Transactions
     
     ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581951834/bankcard_lgk94w.png)
     ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581951834/transaction_u3vldo.png)
     
     * Before making a Transaction, users need to TopUp their wallet by calling a 3rd party API
     
     ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581951989/topup_v7yirn.png)
	 
	 * Now that users have added money to their Balance and made a successful Transaction, they can view information about Wallet movement from the Transactions Page
	 
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581952048/transactions_zvjcfr.png)
     
	 * Bank Card has expired. No Money on Card because the wife has left you for another man. No problem. Just go to the Bank Cards section, where you can manage your Bank Card by updating it or removing it.
	 
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581952468/bankcard-register_nfziw9.png)
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581952468/bankcard-update_lqfb9s.png)
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581952468/bankcard-update-success_dhnbyk.png)
	 
	 * Users can also create Multiple Wallets and can also make a Shared Wallet between each other.
	 
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581954821/wallets_mcaakp.png)
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581954821/wallets-create_lf84n9.png)
	 
	 * Users can edit their profile information from the My Profile page
	 
	 ![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581954928/profile_k17dwj.png)

#### Administrative Part
* Admins can:
     * Manager Users – Block/Unblock
     * View All Transactions     

![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581955156/admin-users_ewp5cj.png)

![Alt text](https://res.cloudinary.com/dqobbbrva/image/upload/v1581955156/admin-transactions_cpuigy.png)

## Technologies
* Spring Web
* Spring Data
* Spring Security
* Thymeleaf
* MySql
* Jpa/Hibernate
* AJAX
* JavaScript / jQuery
* HTML
* CSS
* Bootstrap

## Notes
* Mockito and JUnit for testing
* DTO (data transfer objects) used
* Server-side pagination present
* 80% Unit test code coverage of the business logic
