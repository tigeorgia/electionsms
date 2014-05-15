package com.tigeorgia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tigeorgia.dao.DraftlawDAO;
import com.tigeorgia.model.Draftlaw;


@Repository
public class DraftlawDAOImpl implements DraftlawDAO {
	
	@Autowired  
	private SessionFactory sessionFactory;  

	private Session getCurrentSession(){ 
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void addDraftlaw(Draftlaw draftLaw) {
		getCurrentSession().save(draftLaw);
	}

	@Override
	public Draftlaw getDraftlaw(int id) {
		Draftlaw draftLaw = (Draftlaw) getCurrentSession().get(Draftlaw.class, id);
		return draftLaw;
	}
	
	@Override
	public Draftlaw getDraftlaw(String registrationNumber) {  
        List<Draftlaw> numberList = new ArrayList<Draftlaw>();  
        Query query = getCurrentSession().createQuery("from Draftlaw d where d.billNumber = :registrationNumber"); 
        if (query != null){
        	query.setParameter("registrationNumber", registrationNumber);  
	        numberList = query.list();  
	        if (numberList.size() > 0){  
	            return numberList.get(0);  
	        }else{  
	            return null;
	        }
	    }else{        	
        		return null;
        }
    }  

	@Override
	public void updateDraftlaw(Draftlaw draftLaw) {
		getCurrentSession().update(draftLaw);

	}

	@Override
	public List<Draftlaw> getAllDraftlaw() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Draftlaw> getDraftlaws(List<Integer> idList) {
		// TODO Auto-generated method stub
		return null;
	}

}
