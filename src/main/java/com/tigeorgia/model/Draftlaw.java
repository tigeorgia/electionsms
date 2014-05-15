package com.tigeorgia.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity  
@Table(name="draftlaw_draftlaw")
public class Draftlaw {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "bureau_date")
	private String bureauDate;
	
	@Column(name = "bill_number")
	private String billNumber;
	
	private transient String registrationDate;
	
	private transient String draftlawType;
	
	private transient String draftlawTypeEn;
	
	private transient String primaryParentDraftLaw;
	
	private transient String primaryParentDraftLawEn;
	
	private String title;
	
	private String initiator;
	
	private String author;
	
	private String status;
	
	@Column(name = "shortstatus")
	private String shortStatus;
	
	private String summary;
	
	@Column(name = "full_text")
	private String fullText;
	
	@Column(name = "full_text_url")
	private String fullTextUrl; 
	
	@Column(name = "enacted_text_url")
	private String enactedTextUrl;
	
	@Column(name = "law_number")
	private String lawNumber;
	
	@Column(name = "voting_record_id")
	private Integer votingRecordId;
	
	@Column(name = "related_1")
	private String relatedOne; 
	
	@Column(name = "related_2")
	private String relatedTwo;
	
	@Column(name = "related_3")
	private String relatedThree;
	
	@Column(name = "related_4")
	private String relatedFour;
	
	@Column(name = "related_5")
	private String relatedFive;
	
	@Column(name = "enable_annotations")
	private boolean enableAnnotations;
	
	@Column(name = "moderate_annotations")
	private Date moderateAnnotations;
	
	@Column(name = "title_en")
	private String titleEn;
	
	@Column(name = "title_ka")
	private String titleKa;
	
	@Column(name = "initiator_en")
	private String initiatorEn;
	
	@Column(name = "initiator_ka")
	private String initiatorKa;
	
	@Column(name = "author_en")
	private String authorEn;
	
	@Column(name = "author_ka")
	private String authorKa;
	
	@Column(name = "status_en")
	private String statusEn;
	
	@Column(name = "status_ka")
	private String statusKa;
	
	@Column(name = "summary_en")
	private String summaryEn;
	
	@Column(name = "summary_ka")
	private String summaryKa;
	
	private transient String leadingCommitteeEn;
	
	private transient String leadingCommitteeKa;
	
	@Column(name = "full_text_en")
	private String fullTextEn;
	
	@Column(name = "full_text_ka")
	private String fullTextKa;
	
	private String slug;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBureauDate() {
		return bureauDate;
	}

	public void setBureauDate(String bureauDate) {
		this.bureauDate = bureauDate;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShortStatus() {
		return shortStatus;
	}

	public void setShortStatus(String shortStatus) {
		this.shortStatus = shortStatus;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getFullTextUrl() {
		return fullTextUrl;
	}

	public void setFullTextUrl(String fullTextUrl) {
		this.fullTextUrl = fullTextUrl;
	}

	public String getEnactedTextUrl() {
		return enactedTextUrl;
	}

	public void setEnactedTextUrl(String enactedTextUrl) {
		this.enactedTextUrl = enactedTextUrl;
	}

	public String getLawNumber() {
		return lawNumber;
	}

	public void setLawNumber(String lawNumber) {
		this.lawNumber = lawNumber;
	}

	public String getRelatedOne() {
		return relatedOne;
	}

	public void setRelatedOne(String relatedOne) {
		this.relatedOne = relatedOne;
	}

	public String getRelatedTwo() {
		return relatedTwo;
	}

	public void setRelatedTwo(String relatedTwo) {
		this.relatedTwo = relatedTwo;
	}

	public String getRelatedThree() {
		return relatedThree;
	}

	public void setRelatedThree(String relatedThree) {
		this.relatedThree = relatedThree;
	}

	public String getRelatedFour() {
		return relatedFour;
	}

	public void setRelatedFour(String relatedFour) {
		this.relatedFour = relatedFour;
	}

	public String getRelatedFive() {
		return relatedFive;
	}

	public void setRelatedFive(String relatedFive) {
		this.relatedFive = relatedFive;
	}

	public boolean isEnableAnnotations() {
		return enableAnnotations;
	}

	public void setEnableAnnotations(boolean enableAnnotations) {
		this.enableAnnotations = enableAnnotations;
	}

	public Date getModerateAnnotations() {
		return moderateAnnotations;
	}

	public void setModerateAnnotations(Date moderateAnnotations) {
		this.moderateAnnotations = moderateAnnotations;
	}

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}

	public String getTitleKa() {
		return titleKa;
	}

	public void setTitleKa(String titleKa) {
		this.titleKa = titleKa;
	}

	public String getInitiatorEn() {
		return initiatorEn;
	}

	public void setInitiatorEn(String initiatorEn) {
		this.initiatorEn = initiatorEn;
	}

	public String getInitiatorKa() {
		return initiatorKa;
	}

	public void setInitiatorKa(String initiatorKa) {
		this.initiatorKa = initiatorKa;
	}

	public String getAuthorEn() {
		return authorEn;
	}

	public void setAuthorEn(String authorEn) {
		this.authorEn = authorEn;
	}

	public String getAuthorKa() {
		return authorKa;
	}

	public void setAuthorKa(String authorKa) {
		this.authorKa = authorKa;
	}

	public String getStatusEn() {
		return statusEn;
	}

	public void setStatusEn(String statusEn) {
		this.statusEn = statusEn;
	}

	public String getStatusKa() {
		return statusKa;
	}

	public void setStatusKa(String statusKa) {
		this.statusKa = statusKa;
	}

	public String getSummaryEn() {
		return summaryEn;
	}

	public void setSummaryEn(String summaryEn) {
		this.summaryEn = summaryEn;
	}

	public String getSummaryKa() {
		return summaryKa;
	}

	public void setSummaryKa(String summaryKa) {
		this.summaryKa = summaryKa;
	}

	public String getFullTextEn() {
		return fullTextEn;
	}

	public void setFullTextEn(String fullTextEn) {
		this.fullTextEn = fullTextEn;
	}

	public String getFullTextKa() {
		return fullTextKa;
	}

	public void setFullTextKa(String fullTextKa) {
		this.fullTextKa = fullTextKa;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Integer getVotingRecordId() {
		return votingRecordId;
	}

	public void setVotingRecordId(Integer votingRecordId) {
		this.votingRecordId = votingRecordId;
	}

	@Transient
	public String getDraftlawType() {
		return draftlawType;
	}

	public void setDraftlawType(String draftlawType) {
		this.draftlawType = draftlawType;
	}

	@Transient
	public String getDraftlawTypeEn() {
		return draftlawTypeEn;
	}

	public void setDraftlawTypeEn(String draftlawTypeEn) {
		this.draftlawTypeEn = draftlawTypeEn;
	}

	@Transient
	public String getPrimaryParentDraftLaw() {
		return primaryParentDraftLaw;
	}

	public void setPrimaryParentDraftLaw(String primaryParentDraftLaw) {
		this.primaryParentDraftLaw = primaryParentDraftLaw;
	}

	@Transient
	public String getPrimaryParentDraftLawEn() {
		return primaryParentDraftLawEn;
	}

	public void setPrimaryParentDraftLawEn(String primaryParentDraftLawEn) {
		this.primaryParentDraftLawEn = primaryParentDraftLawEn;
	}

	@Transient
	public String getLeadingCommitteeEn() {
		return leadingCommitteeEn;
	}

	public void setLeadingCommitteeEn(String leadingCommitteeEn) {
		this.leadingCommitteeEn = leadingCommitteeEn;
	}

	@Transient
	public String getLeadingCommitteeKa() {
		return leadingCommitteeKa;
	}

	public void setLeadingCommitteeKa(String leadingCommitteeKa) {
		this.leadingCommitteeKa = leadingCommitteeKa;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	

}
