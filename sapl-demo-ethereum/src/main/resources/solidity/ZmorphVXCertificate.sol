pragma solidity >=0.5.0 <0.7.0;

contract ZmorphVXCertificate {

  // The certification authority decides who can issue a certificate
  address public certificationAuthority;

  string public certificateName = "Zmorph VX Certificate";

  uint public timeValid = 365 days;

  struct Certificate {
    bool obtained;
    address issuer;
    uint issueTime;
  }

  // contains true for addresses that are authorized to issue a certificate
  mapping (address => bool) authorizedIssuers;

  // contains all certificates that have been issued
  mapping (address => Certificate) certificateHolders;

  // The creator of the contract is also the certification authority
  constructor() public {
    certificationAuthority = msg.sender;
  }

  function issueCertificate (address graduate) public {
    require(
      authorizedIssuers[msg.sender],
      "Only the authorized issuers can issue certificates."
    );

    certificateHolders[graduate].obtained = true;
    certificateHolders[graduate].issuer = msg.sender;
    // The issue time is the timestamp of the block which contains the
    // transaction that actually issues the certificate
    certificateHolders[graduate].issueTime = block.timestamp;
  }

  function revokeCertificate (address graduate) public {
    require(
      certificateHolders[graduate].issuer == msg.sender,
      "Only the issuer can revoke the certificate."
      );
    certificateHolders[graduate].obtained = false;
  }


  function hasCertificate(address graduate) public view
          returns (bool certificateOwned) {
    // verifies if the certificate is still valid
    // here block.timestamp refers to the timestamp of the block the request
    // is made to (usually the latest)
    if (block.timestamp < certificateHolders[graduate].issueTime + timeValid) {
      return certificateHolders[graduate].obtained;
    }
    return false;
  }

  function addIssuer (address newIssuer) public {
    require(
      msg.sender == certificationAuthority,
      "Only the Certification Authority can name new certificate issuers."
      );
    authorizedIssuers[newIssuer] = true;
  }

  function removeIssuer (address issuerToRemove) public {
    require(
      msg.sender == certificationAuthority,
      "Only the Certification Authority can remove certificate issuers."
      );
    authorizedIssuers[issuerToRemove] = false;
  }

}
