package com.tigeorgia.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity  
@Table(name="draftlaw_draftlawdiscussion")
public class DraftlawDiscussion {
	
	@Id
	@GeneratedValue
	private Integer id;
		
	@Column(name = "date")
	private Date discussionDate;
	
	/*@ManyToOne
    @JoinColumn(name="draftlaw_id")
	private Draftlaw draftlaw;*/
	
	private Integer draftlaw_id;
	
	private Integer stage;
	
	private String place;
	
	private String place_en;
	
	private String place_ka;
	
	private String passed;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/*public Draftlaw getDraftlaw() {
		return draftlaw;
	}

	public void setDraftlaw(Draftlaw draftlaw) {
		this.draftlaw = draftlaw;
	}*/

	public Date getDiscussionDate() {
		return discussionDate;
	}

	public void setDiscussionDate(Date discussionDate) {
		this.discussionDate = discussionDate;
	}

	public Integer getStage() {
		return stage;
	}

	public void setStage(Integer stage) {
		this.stage = stage;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPlace_en() {
		return place_en;
	}

	public void setPlace_en(String place_en) {
		this.place_en = place_en;
	}

	public String getPlace_ka() {
		return place_ka;
	}

	public void setPlace_ka(String place_ka) {
		this.place_ka = place_ka;
	}

	public String getPassed() {
		return passed;
	}

	public void setPassed(String passed) {
		this.passed = passed;
	}

	public Integer getDraftlaw_id() {
		return draftlaw_id;
	}

	public void setDraftlaw_id(Integer draftlaw_id) {
		this.draftlaw_id = draftlaw_id;
	}
	
}
