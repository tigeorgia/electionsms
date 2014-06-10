package com.tigeorgia.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tigeorgia.dao.DraftlawDiscussionDAO;
import com.tigeorgia.model.DraftlawDiscussion;
import com.tigeorgia.service.DraftlawDiscussionService;

@Service
@Transactional
public class DraftlawDiscussionServiceImpl implements DraftlawDiscussionService{
	
	@Autowired
	private DraftlawDiscussionDAO draftLawDiscussionDAO;

	@Override
	public void addDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion) {
		draftLawDiscussionDAO.addDraftlawDiscussion(draftLawDiscussion);
	}

	@Override
	public DraftlawDiscussion getDraftlawDiscussion(int id) {
		return draftLawDiscussionDAO.getDraftlawDiscussion(id);
	}

	@Override
	public List<DraftlawDiscussion> getDraftlawDiscussionByDraftLawId(Integer draftLawId) {
		return draftLawDiscussionDAO.getDraftlawDiscussionByDraftLawId(draftLawId);
	}

	@Override
	public void updateDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion) {
		draftLawDiscussionDAO.updateDraftlawDiscussion(draftLawDiscussion);
	}

	@Override
	public List<DraftlawDiscussion> getDraftlawDiscussions(List<Integer> idList) {
		return draftLawDiscussionDAO.getDraftlawDiscussions(idList);
	}

}
