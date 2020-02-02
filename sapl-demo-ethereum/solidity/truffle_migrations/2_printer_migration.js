var GraftenOneCertificate = artifacts.require("GraftenOneCertificate");
var Ultimaker2ExtendedCertificate = artifacts.require("Ultimaker2ExtendedCertificate");
var ZmorphVXCertificate = artifacts.require("ZmorphVXCertificate");

module.exports = function(deployer) {
  deployer.deploy(GraftenOneCertificate);
  deployer.deploy(Ultimaker2ExtendedCertificate);
  deployer.deploy(ZmorphVXCertificate);
};
