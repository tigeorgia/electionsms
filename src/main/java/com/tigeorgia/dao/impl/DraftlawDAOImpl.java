package com.tigeorgia.dao.impl;

import java.util.List;

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
		// TODO Auto-generated method stub

	}

	@Override
	public Draftlaw getDraftlaw(int id) {
		Draftlaw draftLaw = (Draftlaw) getCurrentSession().get(Draftlaw.class, id);
		return draftLaw;
	}

	@Override
	public void updateDraftlaw(Draftlaw draftLaw) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Draftlaw> getAllDraftlaw() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Draftlaw> getStores(List<Integer> idList) {
		// TODO Auto-generated method stub
		return null;
	}

}
