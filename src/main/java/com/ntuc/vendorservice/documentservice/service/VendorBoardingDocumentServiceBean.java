package com.ntuc.vendorservice.documentservice.service;

import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;
import org.apache.commons.lang.NotImplementedException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class VendorBoardingDocumentServiceBean implements VendorBoardingDocumentService{

    @Override
    public Result getBPMDocumentByName(String value, String value1) {
        throw new NotImplementedException();
    }
}
