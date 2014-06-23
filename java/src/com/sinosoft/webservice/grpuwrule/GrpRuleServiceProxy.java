package com.sinosoft.webservice.grpuwrule;

public class GrpRuleServiceProxy implements com.sinosoft.webservice.grpuwrule.GrpRuleService {
  private String _endpoint = null;
  private com.sinosoft.webservice.grpuwrule.GrpRuleService grpRuleService = null;
  
  public GrpRuleServiceProxy() {
    _initGrpRuleServiceProxy();
  }
  
  private void _initGrpRuleServiceProxy() {
    try {
      grpRuleService = (new com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplServiceLocator()).getGrpRuleServiceImplPort();
      if (grpRuleService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)grpRuleService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)grpRuleService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (grpRuleService != null)
      ((javax.xml.rpc.Stub)grpRuleService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.sinosoft.webservice.grpuwrule.GrpRuleService getGrpRuleService() {
    if (grpRuleService == null)
      _initGrpRuleServiceProxy();
    return grpRuleService;
  }
  
  public java.lang.String grpUWReq(java.lang.String arg0) throws java.rmi.RemoteException{
    if (grpRuleService == null)
      _initGrpRuleServiceProxy();
    return grpRuleService.grpUWReq(arg0);
  }
  
  
}