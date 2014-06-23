/**
 * GrpRuleServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.sinosoft.webservice.grpuwrule;

import com.sinosoft.lis.db.LDSysVarDB;

public class GrpRuleServiceImplServiceLocator extends org.apache.axis.client.Service implements com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplService {

    public GrpRuleServiceImplServiceLocator() {
    	setGrpRuleServiceImplPortAddress();
    }


    public GrpRuleServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
        setGrpRuleServiceImplPortAddress();
    }

    public GrpRuleServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
        setGrpRuleServiceImplPortAddress();
    }

    // Use to get a proxy class for GrpRuleServiceImplPort
    private java.lang.String GrpRuleServiceImplPort_address = "";

    public java.lang.String getGrpRuleServiceImplPortAddress() {
        return GrpRuleServiceImplPort_address;
    }
    //通过数据库配置webservice的地址 add by frost
    public void setGrpRuleServiceImplPortAddress() {
        LDSysVarDB tLDSysVarDB = new LDSysVarDB();
        tLDSysVarDB.setSysVar("RuleService_Address");
        if(tLDSysVarDB.getInfo()){
        	GrpRuleServiceImplPort_address = tLDSysVarDB.getSysVarValue();
        }
   }

    // The WSDD service name defaults to the port name.
    private java.lang.String GrpRuleServiceImplPortWSDDServiceName = "GrpRuleServiceImplPort";

    public java.lang.String getGrpRuleServiceImplPortWSDDServiceName() {
        return GrpRuleServiceImplPortWSDDServiceName;
    }
    
    public void setGrpRuleServiceImplPortWSDDServiceName(java.lang.String name) {
        GrpRuleServiceImplPortWSDDServiceName = name;
    }
    

    public com.sinosoft.webservice.grpuwrule.GrpRuleService getGrpRuleServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GrpRuleServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGrpRuleServiceImplPort(endpoint);
    }

    public com.sinosoft.webservice.grpuwrule.GrpRuleService getGrpRuleServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplServiceSoapBindingStub _stub = new com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getGrpRuleServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGrpRuleServiceImplPortEndpointAddress(java.lang.String address) {
        GrpRuleServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.sinosoft.webservice.grpuwrule.GrpRuleService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplServiceSoapBindingStub _stub = new com.sinosoft.webservice.grpuwrule.GrpRuleServiceImplServiceSoapBindingStub(new java.net.URL(GrpRuleServiceImplPort_address), this);
                _stub.setPortName(getGrpRuleServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GrpRuleServiceImplPort".equals(inputPortName)) {
            return getGrpRuleServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://server.grprule.gc.com/", "GrpRuleServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://server.grprule.gc.com/", "GrpRuleServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GrpRuleServiceImplPort".equals(portName)) {
            setGrpRuleServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
