package org.crossfit.app.service.payments.external;

import org.joda.time.LocalDate;

public class MemberMandateDTO {

	private String code;
	private String email;
	private String iban;
	private String banqueNom;
	private String banqueCodePays;
	private String banqueBIC;
	private String mandateRef;
	private String mandateType;
	private LocalDate mandatDateSignature;
	private String mandateICS;
	
	private LocalDate dateExecution;
	private int montant;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getBanqueNom() {
		return banqueNom;
	}
	public void setBanqueNom(String banqueNom) {
		this.banqueNom = banqueNom;
	}
	public String getBanqueCodePays() {
		return banqueCodePays;
	}
	public void setBanqueCodePays(String banqueCodePays) {
		this.banqueCodePays = banqueCodePays;
	}
	public String getBanqueBIC() {
		return banqueBIC;
	}
	public void setBanqueBIC(String banqueBIC) {
		this.banqueBIC = banqueBIC;
	}
	public LocalDate getDateExecution() {
		return dateExecution;
	}
	public void setDateExecution(LocalDate dateExecution) {
		this.dateExecution = dateExecution;
	}
	public String getMandateRef() {
		return mandateRef;
	}
	public void setMandateRef(String mandateRef) {
		this.mandateRef = mandateRef;
	}
	public String getMandateType() {
		return mandateType;
	}
	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}
	public LocalDate getMandatDateSignature() {
		return mandatDateSignature;
	}
	public void setMandatDateSignature(LocalDate mandatDateSignature) {
		this.mandatDateSignature = mandatDateSignature;
	}
	public String getMandateICS() {
		return mandateICS;
	}
	public void setMandateICS(String mandateICS) {
		this.mandateICS = mandateICS;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getMontant() {
		return montant;
	}
	public void setMontant(int montant) {
		this.montant = montant;
	}	
}
