package com.tigeorgia.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tigeorgia.dao.DraftlawDAO;
import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.service.DraftlawService;

@Service
@Transactional
public class DraftlawServiceImpl implements DraftlawService {
	
	@Autowired
	private DraftlawDAO draftLawDAO;

	@Override
	public void addDraftlaw(Draftlaw draftLaw) {
		draftLawDAO.addDraftlaw(draftLaw);
	}

	@Override
	public Draftlaw getDraftlaw(int id) {
		return draftLawDAO.getDraftlaw(id);
	}

	@Override
	public void updateDraftlaw(Draftlaw draftLaw) {
		draftLawDAO.updateDraftlaw(draftLaw);
		
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

	@Override
	public Draftlaw getDraftlaw(String registrationNumber) {
		return draftLawDAO.getDraftlaw(registrationNumber);
	}

}
