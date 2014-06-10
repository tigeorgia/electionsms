package com.tigeorgia.dao;

import java.util.List;

import com.tigeorgia.model.DraftlawDiscussion;

public interface DraftlawDiscussionDAO {
	
	public void addDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion);
	public DraftlawDiscussion getDraftlawDiscussion(int id);
	public List<DraftlawDiscussion> getDraftlawDiscussionByDraftLawId(Integer draftLawId);
	public void updateDraftlawDiscussion(DraftlawDiscussion draftLawDiscussion);
	public List<DraftlawDiscussion> getDraftlawDiscussions(List<Integer> idList);

}
