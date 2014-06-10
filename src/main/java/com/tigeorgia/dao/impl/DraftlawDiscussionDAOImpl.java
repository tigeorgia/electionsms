package com.tigeorgia.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tigeorgia.dao.DraftlawDiscussionDAO;
import com.tigeorgia.model.DraftlawDiscussion;

@Repository
public class DraftlawDiscussionDAOImpl implements DraftlawDiscussionDAO{
	
	@Autowired  
	private SessionFactory sessionFactory;  

	private Session getCurrentSession(){ 
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void addDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion) {
		getCurrentSession().save(draftLawDiscussion);
	}

	@Override
	public DraftlawDiscussion getDraftlawDiscussion(int id) {
		DraftlawDiscussion discussion = (DraftlawDiscussion) getCurrentSession().get(DraftlawDiscussion.class, id);
		return discussion;
	}

	@Override
	public List<DraftlawDiscussion> getDraftlawDiscussionByDraftLawId(Integer draftLawId) {
		List<DraftlawDiscussion> resultList = new ArrayList<DraftlawDiscussion>();  
        Query query = getCurrentSession().createQuery("from DraftlawDiscussion d where d.draftlaw_id = :draftlawid"); 
        if (query != null){
        	query.setParameter("draftlawid", draftLawId);  
        	resultList = query.list();  
	        if (resultList.size() > 0){  
	            return resultList;  
	        }else{  
	            return null;
	        }
	    }else{        	
        		return null;
        }
	}

	@Override
	public void updateDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion) {
		getCurrentSession().update(draftLawDiscussion);
	}

	@Override
	public List<DraftlawDiscussion> getDraftlawDiscussions(List<Integer> idList) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
