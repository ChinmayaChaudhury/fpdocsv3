package com.ntuc.vendorservice.foundationcontext.repository;

import java.util.AbstractList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.Parameter;
 
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

public class PagingList<E> extends AbstractList<E> { 
    public static final String CURRENT_PAGE = "pageNum";

	public static final String COLLECTION = "collection";

	public static final String TOTAL_PAGES = "totalPages";

	/**
     * The size determined by {@link #calculateSize()}
     */
    private int size;

    /**
     * Provided page size
     */
    private int pageSize;

    /**
     * Original query. This query is closed for size calculation and re-used
     * with different first/max values for each page result.
     */
    private TypedQuery<E> typedQuery;
    
    /**
     * Cached page results. This approach holds all retrieved pages. if the
     * total volume of results is large this caching should be changed to only
     * hold a limited set of pages or rely on garbage collection to clear out
     * unused pages.
     */
    private List<E>[] pages;

    @SuppressWarnings("unchecked")
    public PagingList(TypedQuery<E> typedQuery, int pageSize) {
        this.typedQuery = typedQuery;
        this.pageSize = pageSize;
        this.size = calculateSize();
        this.pages = new List[(this.size / this.pageSize) + (this.size % this.pageSize > 0 ? 1 : 0)];
    }

    @Override
    public E get(int index) {
        return getPage(index / this.pageSize).get(index % this.pageSize);
    }

    @Override
    public int size() {
        return this.size;
    }

    protected TypedQuery<E> getQuery() {
        return typedQuery;
    }

    public List<E>[] getPages() {
        return pages;
    }

    public List<E> getPage(int pageNum) {
        List<E> page = getPages()[pageNum]; 
        if (page == null) {
            getQuery().setFirstResult(this.pageSize * pageNum);
            getQuery().setMaxResults((this.pageSize * pageNum) + this.pageSize);
            page = getQuery().getResultList();
            getPages()[pageNum] = page;
        }

        return page;
    }

    /**
     * Using the provided {@link TypedQuery} to calculate the size. The query is
     * copied to create a new query which just retrieves the count.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int calculateSize() {
        JpaQuery<E> queryImpl = (JpaQuery<E>) getQuery();
        ReadAllQuery raq = JpaHelper.getReadAllQuery(getQuery()); 
        
        ReportQuery rq = null;

        if (raq.isReportQuery()) {
            rq = (ReportQuery) raq.clone();
            rq.getItems().clear();
            rq.addCount();
            rq.getGroupByExpressions().clear();
            rq.getOrderByExpressions().clear();
        } else {
            rq = new ReportQuery();
            rq.setReferenceClass(raq.getReferenceClass());
            rq.addCount();
            rq.setShouldReturnSingleValue(true);
            rq.setSelectionCriteria(raq.getSelectionCriteria());
        }
        // Wrap new report query as JPA query for execution with parameters
        TypedQuery<Number> countQuery = (TypedQuery<Number>) JpaHelper.createQuery(rq, queryImpl.getEntityManager());

        // Copy parameters
        for (Parameter p : getQuery().getParameters()) {
            countQuery.setParameter(p, getQuery().getParameterValue(p));
        }

        return countQuery.getSingleResult().intValue();
    }



}
