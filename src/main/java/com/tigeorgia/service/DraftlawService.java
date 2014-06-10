package com.tigeorgia.service;

import java.util.List;

import com.tigeorgia.model.Draftlaw;

public interface DraftlawService {
	
	public void addDraftlaw(Draftlaw draftLaw);
	public Draftlaw getDraftlaw(int id);
	public Draftlaw getDraftlaw(String registrationNumber);
	public Draftlaw getDraftlawWithDiscussions(String registrationNumber);
	public void updateDraftlaw(Draftlaw draftLaw);
	public List<Draftlaw> getAllDraftlaw();
	public List<Draftlaw> getDraftlaws(List<Integer> idList);

}
