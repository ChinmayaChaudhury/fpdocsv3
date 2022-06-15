package com.ntuc.vendorservice.documentservice.models;


import com.ntuc.vendorservice.foundationcontext.catalog.model.KeyVal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BPMDocument implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String createdBy;
    private String openCmisID;
    private String openCmisParentID;
    private Integer childCount;
    private String documentPath;
    private Double documentSize;
    private String contentType;
    private List<KeyVal> metadata;
    private List<BPMDocument> collection;
    private Date createDate;
    private String sourcePath;
    /**
     * @return the folderPath
     */
    public String getSourcePath() {
        return sourcePath;
    }
    /**
     * @param folderPath the Path to set
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }
    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the openCmisID
     */
    public String getOpenCmisID() {
        return openCmisID;
    }
    /**
     * @param openCmisID the openCmisID to set
     */
    public void setOpenCmisID(String openCmisID) {
        this.openCmisID = openCmisID;
    }
    /**
     * @return the openCmisParentID
     */
    public String getOpenCmisParentID() {
        return openCmisParentID;
    }
    /**
     * @param openCmisParentID the openCmisParentID to set
     */
    public void setOpenCmisParentID(String openCmisParentID) {
        this.openCmisParentID = openCmisParentID;
    }
    /**
     * @return the metadata
     */
    public List<KeyVal> getMetadata() {
        if(metadata==null){
            metadata=new ArrayList<KeyVal>();
        }
        return metadata;
    }
    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(List<KeyVal> metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the childCount
     */
    public Integer getChildCount() {
        return childCount;
    }
    /**
     * @param childCount the childCount to set
     */
    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    /**
     * @return the documentPath
     */
    public String getDocumentPath() {
        return documentPath;
    }
    /**
     * @param documentPath the documentPath to set
     */
    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
    /**
     * @return the documentSize
     */
    public Double getDocumentSize() {
        return documentSize;
    }
    /**
     * @param documentSize the documentSize to set
     */
    public void setDocumentSize(Double documentSize) {
        this.documentSize = documentSize;
    }
    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }
    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    /**
     * @return the collection
     */
    public List<BPMDocument> getCollection() {
        return collection;
    }
    /**
     * @param collection the collection to set
     */
    public void setCollection(List<BPMDocument> collection) {
        this.collection = collection;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((openCmisID == null) ? 0 : openCmisID.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BPMDocument other = (BPMDocument) obj;
        if (openCmisID == null) {
            if (other.openCmisID != null)
                return false;
        } else if (!openCmisID.equals(other.openCmisID))
            return false;
        return true;
    }


}
