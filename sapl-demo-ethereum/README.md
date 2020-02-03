# SAPL Ethereum Demo
This documentation shows how to start the SAPL Ethereum Demo and explains its features. The demo is about demonstrating the usage of the [Ethereum Policy Information Point](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-ethereum) (EthereumPIP).
If you are using the preconfigured Virtual Box Image, you can skip the Getting started section and directly head to the features of the demo application.


## Getting started
First, you should clone this repository with

```
git clone https://github.com/heutelbeck/sapl-demos.git
```
### Basic requirements
To run this demo, you should have node.js and npm installed on your system. Furthermore, an actual version of maven is required.

### Installing truffle
For deploying our contracts we will use truffle, since it combines very vell with Ganache. You can install the truffle suite as stated [here](https://www.trufflesuite.com/truffle). Once installed, create an empty directory for your truffle workspace. Change to this directory in a terminal and run 

```
truffle init
```
Now there should have appeared various folders in your truffle workspace and a `truffle-config.js` file. Open this file and replace the contents with:

```javascript
module.exports = {
  networks: {
    development: {
      host: "127.0.0.1",
      port: 8545,
      network_id: "*"
    }
  }
};
```

Now go into the `sapl-demos` repository and open the `sapl-demo-ethereum` folder. 
- There you will find a folder called `solidity`. Copy all contract files (ending with .sol) in there into the folder `contracts` of your truffle workspace. 
- In the `solidity` folder there also is a folder called `truffle_migrations`. Copy the file in there into the `migrations` folder of your truffle workspace. 

### Setting up the testnet with Ganache
Then you have to download the Ganache client from [here](https://www.trufflesuite.com/ganache).

- Start the ganache client and select **New workspace**. 
- Choose a name for your workspace. 
- Click on the **Add project** button below. 
- Select the **truffle-config.js** file you just modified and confirm. 
- Then select the tab **Server** and set the portnumber to 8545. 
- After that, select the **Accounts & Keys** tab and enter the following mnemonic:

```
defense decade prosper portion dove educate sing auction camera minute sing loyal
```
This way you will create the accounts used for the demo application. Then click on **Save Workspace** and the blockchain will be started automatically.

### Deploying the contracts with truffle


Now go to the truffle workspace in a terminal and run

```
truffle migrate
```
This will deploy the contracts to the blockchain.

While we could create the correct accounts with the mnemonic, addresses of the deployed contracts cannot be predicted. Therefore, we need to add them manually to our configuration. Open the following file in the repository:

```
sapl-demos/sapl-demo-ethereum/src/main/resources/policies/pdp.json
```
You will find entries for the printers of the demo application here. We need to replace the contract addresses with the ones from our new blockchain. Navigate to the contracts tab in Ganache. You will find the deployed contracts here. Click on each contract and then copy the **address** into the corresponding field of the `pdp.json`.

Now we nearly finished our configuration. The last thing we have to do, is to appoint our accreditation authority, so that it also can be an issuer of certificates. To do so, navigate to the `sapl-demo-ethereum` project in a terminal and first run

```
mvn clean install
```
Once completed, run

```
mvn exec:java -Dexec.mainClass="org.demo.helper.EthInitContracts"
```
to set up the contracts correctly.

Now finally we can start the demo application with

```
mvn spring-boot:run
```

## Domain of the demo application
The scenario of the demo is, that a service for 3D printers wants to verify, that the user of the printers also have a valid certificate to use them. These certificates are created by an accreditation authority, which in turn can name issuers that are allowed to issue certificates. In this demo application, we simply assume, that the accrediation authority is in charge of all three contracts. In a real world scenario, there would be different authorities for each certificate. For simplification, we also have set up the accreditation authority to be the issuer of certificates.

The user in our application can choose from different printers and from different templates.

This demo application can be combined with another demo, which is still in development, so that the certificates are issued by this other application. It can also be standalone because the certificates can be issued directly from the user interface.

## Features of the demo application
In this section we will present all features the demo application has to offer. After starting the application as described above, open a browser and navigate to `localhost:8080`. You will find a login screen. There are two users that can log into the demo application:

```
Username:  Alice
Password: Greenfield

Username: Bob
Password: Springsteen
```
Once you logged in, you can see the printer control panel.

### Selecting the printer
You can choose between three different printers. Every printer has his own certificate, so a user can be certified for each one individually. In order to give the user a certificate, click the **Issue Certificate** button. You can also revoke a certificate with **Revoke certificate**. Once the user is certified for the current printer, you can select a template and start the print job. The certification feature demonstrates the use of the **contract** function of the EthereumPIP. A policy for using this function looks like this:

```
policy "printWithZmorphVX"
permit
  action=="start" & resource=="Zmorph VX"
where
  var request = {"address":subject.ethereumAddress, "printer":resource};
  request.<printer.certified>;
```

### The paid template feature
Not all templates are available by default. A user can make a payment to buy an additional template by clicking the **Pay** button. Thereby the **cubes** template will be unlocked. This feature demonstrates the use of the EthereumPip's **transaction** function, which lets us verify, if a transaction has taken place. The corresponding policy looks like this:

```
policy "paidTemplate"
permit
  action=="access" & resource=="paidTemplate"
where
  var request = {
                 "transactionHash":subject.transactionHash,
                 "fromAccount":subject.ethereumAddress,
                 "toAccount":subject.<ethereum.coinbase>,
                 "transactionValue":1000000000000000000
                 };
  request.<ethereum.transaction>;
```

### The crowdfunding feature
Another template will only be unlocked, if the application owner's account (which is the first in the accounts list of our Ganache client) holds a certain amount of Ether. This decision does not depend on an attribute of the currently logged in user, but can also be expressed in the policies:

```
policy "crowdTemplate"
permit
  action=="access" & resource=="crowdTemplate"
where
  var request = {"address":subject.<ethereum.coinbase>};
  request.<ethereum.balance> >= 105000000000000000000;
```
With the **coinbase** method, we can get the address of the application owner running the node. Then we use the **balance** method, to verify if this address holds a certain amount (in our case more than 105 Ether).

### The domain specific PIP
Maybe you noticed, that the first policy uses another PIP than the other policies. This PIP called **printer** is a domain-specific PIP for our demo application. Since the EthereumPIP provides general functions, it can be useful to implement such a domain-specific PIP to avoid complicated authentication requests. We can easily save the non-changing values of the request there and then call a functoin of the EthereumPIP. Moreover, this way we can adapt the names of PIP and attributes to fit in our domain.
