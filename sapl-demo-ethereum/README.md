# SAPL Ethereum Demo
This documentation shows how to start the SAPL Ethereum Demo and explains its features. The demo is about demonstrating the usage of the [Ethereum Policy Information Point](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-ethereum) (EthereumPIP).


## Getting started
To run this demo, you should have Git, node.js, maven, and npm installed on your system.

First, you should clone this repository with

```
git clone https://github.com/heutelbeck/sapl-demos.git
```

Navigate to the `sapl-demo-ethereum` folder in a terminal or IDE and run 

```
mvn install
```



### Setting up the testnet with Ganache
For our application to work correctly, we need a local testnet.
Therefore, you must download the Ganache client from [here](https://www.trufflesuite.com/ganache).

- Start the ganache client and select **New workspace**. 
- Choose a name for your workspace.
- After that, select the **Accounts & Keys** tab and enter the following mnemonic:

```
defense decade prosper portion dove educate sing auction camera minute sing loyal
```
This way, you will create the accounts used for the demo application. Then click on **Save Workspace**, and the blockchain will be started automatically.

### Start the application

Now we can start the demo application by navigating to the `sapl-demo-ethereum` folder in the repository and then entering:

```
mvn spring-boot:run
```

### Advanced: Deploying and using your contracts
This section is not necessary to run the demo application. It is important in the case that the application should be used with another demo application or if you simply wish to have persistent certificates.

The demo application will generate new contracts on the blockchain each time it is started. Therefore, issued certificates will not persist between runs. But the application also provides the possibility to always use the same contracts. To do so, you have to save the addresses of your deployed contracts in the `variables` of the `pdp.json` configuration file of the PDP, which you can find under `src/main/resources/policies`. Please use the printer names provided in the [MainView](https://github.com/heutelbeck/sapl-demos/blob/master/sapl-demo-ethereum/src/main/java/org/demo/MainView.java). A correct configuration could look like this:

```json
{
  "algorithm": "DENY_UNLESS_PERMIT",
  "variables": {  
                  "ethPollingInterval":100,
                  "Ultimaker 2 Extended+":"0x1Ac704bD40B82E12c4a1808618F4d62a3A457869",
                  "Graften One":"0x6B74dc232B0035A9f6E725B406572A6D9583fa61",
                  "Zmorph VX":"0x5ef552965503CFf922c781b3178f5e4FB3519Fee"
                }
}
```
Now the application will load the contracts from the given addresses instead of deploying new ones. The contracts can be found in the `src/main/resources/solidity` folder of the application. You can deploy them with [Truffle](https://www.trufflesuite.com/truffle) or any other way you like. Please, ensure that you deploy them with the first address in the Ganache testnet `0x3924F456CC0196ff89AAbbD6192289a9B37De73A`. The application relies on this address being the accreditation authority. Furthermore, you have to add this same address as an issuer to each contract if you want the `Issue Certificate` and `Revoke Certificate` buttons in the demo to work.

## Domain of the demo application
The scenario of the demo is that a service for 3D printers wants to verify if the users of the printers also have valid certificates to use them. These certificates are created by an accreditation authority, which in turn can name issuers that are allowed to issue certificates. In this demo application, we simply assume, that the accrediation authority is in charge of all three contracts. In a real-world scenario, there would be different authorities for each certificate. For simplification, we also have set up the accreditation authority to be the issuer of certificates.

The user in our application can choose from different printers and different templates.


## Features of the demo application
In this section, we will present all features the demo application has to offer. After starting the application as described above, open a browser and navigate to `localhost:8080`. You will find a login screen. There are two users that can log into the demo application:

```
Username:  Alice
Password: Greenfield

Username: Bob
Password: Springsteen
```
Once you log in, you can see the printer control panel.

### Selecting the printer
You can choose between three different printers. Every printer has its own certificate so that a user can be certified for each one individually. To give the user a certificate, click the **Issue Certificate** button. You can also revoke a certificate with **Revoke certificate**. Once the user is certified for the current printer, you can select a template and start the print job. The certification feature demonstrates the use of the **contract** function of the EthereumPIP. A policy for using this function looks like this:

```
policy "printWithZmorphVX"
permit
  action=="start" & resource=="Zmorph VX"
where
  var request = {"address":subject.ethereumAddress, "printer":resource};
  request.<printer.certified>;
```

### The paid template feature
Not all templates are available by default. A user can make a payment to buy an additional template by clicking the **Pay** button. Thereby the **cubes** template will be unlocked. This feature demonstrates the use of the EthereumPip's **transaction** function, which lets us verify if a transaction has taken place. The corresponding policy looks like this:

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
Another template will only be unlocked if the application owner's account (which is the first in the accounts list of our Ganache client) holds a certain amount of Ether. This decision does not depend on an attribute of the currently logged-in user but can also be expressed in the policies:

```
policy "crowdTemplate"
permit
  action=="access" & resource=="crowdTemplate"
where
  var request = {"address":subject.<ethereum.coinbase>};
  request.<ethereum.balance> >= 105000000000000000000;
```
With the **coinbase** method, we can get the address of the application owner running the node. Then we use the **balance** method to verify if this address holds a certain amount (more than 105 Ether).

### The domain-specific PIP
Maybe you noticed that the first policy uses another PIP than the other policies. This PIP called **printer** is a domain-specific PIP for our demo application. Since the EthereumPIP provides general functions, it can be helpful to implement such a domain-specific PIP to avoid complicated authentication requests. We can easily save the non-changing values of the request and then call a function of the EthereumPIP. Moreover, this way, we can adapt the names of PIP and attributes to fit in our domain.
