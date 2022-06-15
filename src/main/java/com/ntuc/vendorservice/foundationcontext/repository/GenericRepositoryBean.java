package com.ntuc.vendorservice.foundationcontext.repository;

import java.util.List;

import com.ntuc.vendorservice.foundationcontext.catalog.exceptions.ServiceRepositoryException;;

public interface GenericRepositoryBean<E> {
	
	List<E> findAll();
	
	E add(E e)throws ServiceRepositoryException;
	
	E update(E e)throws ServiceRepositoryException;

}
