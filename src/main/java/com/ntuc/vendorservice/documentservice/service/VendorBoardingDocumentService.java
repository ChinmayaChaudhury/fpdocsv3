package com.ntuc.vendorservice.documentservice.service;

import com.ntuc.vendorservice.foundationcontext.catalog.model.Result;

public interface VendorBoardingDocumentService {
    /**
     *
     * @param confirmationNotification
     * @param bpmDocumentPath
     * @return
     */
    Result getBPMDocumentByName(final String confirmationNotification, final String bpmDocumentPath);
}
