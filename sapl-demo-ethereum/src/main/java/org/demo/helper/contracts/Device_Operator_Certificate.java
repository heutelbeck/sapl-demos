package org.demo.helper.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.0.
 */
public class Device_Operator_Certificate extends Contract {
    private static final String BINARY = "60c0604052601b60808190527f4465766963655f4f70657261746f725f4365727469666963617465000000000060a090815261003e916001919061006b565b506301e2850060025534801561005357600080fd5b50600080546001600160a01b03191633179055610106565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100ac57805160ff19168380011785556100d9565b828001600101855582156100d9579182015b828111156100d95782518255916020019190600101906100be565b506100e59291506100e9565b5090565b61010391905b808211156100e557600081556001016100ef565b90565b610606806101156000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c806347bc70931161005b57806347bc70931461012f5780635f893bfa14610155578063948e19681461017b578063a2d92d66146101f857610088565b80630c02199b1461008d57806320694db0146100c7578063302d6d6b146100ef578063421928e914610109575b600080fd5b6100b3600480360360208110156100a357600080fd5b50356001600160a01b031661021c565b604080519115158252519081900360200190f35b6100ed600480360360208110156100dd57600080fd5b50356001600160a01b0316610270565b005b6100f76102dd565b60408051918252519081900360200190f35b6100ed6004803603602081101561011f57600080fd5b50356001600160a01b03166102e3565b6100ed6004803603602081101561014557600080fd5b50356001600160a01b031661036d565b6100ed6004803603602081101561016b57600080fd5b50356001600160a01b03166103d7565b610183610455565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101bd5781810151838201526020016101a5565b50505050905090810190601f1680156101ea5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6102006104e2565b604080516001600160a01b039092168252519081900360200190f35b6002546001600160a01b03821660009081526004602052604081206001015490910142101561026757506001600160a01b03811660009081526004602052604090205460ff1661026b565b5060005b919050565b6000546001600160a01b031633146102b95760405162461bcd60e51b81526004018080602001828103825260428152602001806104f26042913960600191505060405180910390fd5b6001600160a01b03166000908152600360205260409020805460ff19166001179055565b60025481565b3360009081526003602052604090205460ff166103315760405162461bcd60e51b815260040180806020018281038252603381526020018061055f6033913960400191505060405180910390fd5b6001600160a01b031660009081526004602052604090208054600160ff199091168117610100600160a81b031916610100330217825542910155565b6000546001600160a01b031633146103b65760405162461bcd60e51b81526004018080602001828103825260408152602001806105926040913960400191505060405180910390fd5b6001600160a01b03166000908152600360205260409020805460ff19169055565b6001600160a01b0381811660009081526004602052604090205461010090041633146104345760405162461bcd60e51b815260040180806020018281038252602b815260200180610534602b913960400191505060405180910390fd5b6001600160a01b03166000908152600460205260409020805460ff19169055565b60018054604080516020600284861615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156104da5780601f106104af576101008083540402835291602001916104da565b820191906000526020600020905b8154815290600101906020018083116104bd57829003601f168201915b505050505081565b6000546001600160a01b03168156fe4f6e6c79207468652043657274696669636174696f6e20417574686f726974792063616e206e616d65206e657720636572746966696361746520697373756572732e4f6e6c7920746865206973737565722063616e207265766f6b65207468652063657274696669636174652e4f6e6c792074686520617574686f72697a656420697373756572732063616e206973737565206365727469666963617465732e4f6e6c79207468652043657274696669636174696f6e20417574686f726974792063616e2072656d6f766520636572746966696361746520697373756572732ea265627a7a72305820316db70e247a975202220ff749636f225e3e90a5da72a3d00bd66b691090c97264736f6c63430005090032";

    public static final String FUNC_HASCERTIFICATE = "hasCertificate";

    public static final String FUNC_ADDISSUER = "addIssuer";

    public static final String FUNC_TIMEVALID = "timeValid";

    public static final String FUNC_ISSUECERTIFICATE = "issueCertificate";

    public static final String FUNC_REMOVEISSUER = "removeIssuer";

    public static final String FUNC_REVOKECERTIFICATE = "revokeCertificate";

    public static final String FUNC_CERTIFICATENAME = "certificateName";

    public static final String FUNC_CERTIFICATIONAUTHORITY = "certificationAuthority";

    @Deprecated
    protected Device_Operator_Certificate(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Device_Operator_Certificate(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Device_Operator_Certificate(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Device_Operator_Certificate(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Boolean> hasCertificate(String graduate) {
        final Function function = new Function(FUNC_HASCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, graduate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> addIssuer(String newIssuer) {
        final Function function = new Function(
                FUNC_ADDISSUER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newIssuer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> timeValid() {
        final Function function = new Function(FUNC_TIMEVALID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> issueCertificate(String graduate) {
        final Function function = new Function(
                FUNC_ISSUECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, graduate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeIssuer(String issuerToRemove) {
        final Function function = new Function(
                FUNC_REMOVEISSUER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, issuerToRemove)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeCertificate(String graduate) {
        final Function function = new Function(
                FUNC_REVOKECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, graduate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> certificateName() {
        final Function function = new Function(FUNC_CERTIFICATENAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> certificationAuthority() {
        final Function function = new Function(FUNC_CERTIFICATIONAUTHORITY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static Device_Operator_Certificate load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Device_Operator_Certificate(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Device_Operator_Certificate load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Device_Operator_Certificate(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Device_Operator_Certificate load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Device_Operator_Certificate(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Device_Operator_Certificate load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Device_Operator_Certificate(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Device_Operator_Certificate> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Device_Operator_Certificate.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Device_Operator_Certificate> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Device_Operator_Certificate.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Device_Operator_Certificate> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Device_Operator_Certificate.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Device_Operator_Certificate> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Device_Operator_Certificate.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
