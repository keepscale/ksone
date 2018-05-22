package org.crossfit.app.repository;

public class SearchMemberCriteria {
	public String search = "%";
	public boolean includeActif = true;
	public boolean includeNotEnabled = false;
	public boolean includeBloque = false;
	
	public SearchMemberCriteria() {
		super();
	}

	public SearchMemberCriteria(String search, boolean includeActif, boolean includeNotEnabled,
			boolean includeBloque) {
		super();
		this.search = search;
		this.includeActif = includeActif;
		this.includeNotEnabled = includeNotEnabled;
		this.includeBloque = includeBloque;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public boolean isIncludeActif() {
		return includeActif;
	}

	public void setIncludeActif(boolean includeActif) {
		this.includeActif = includeActif;
	}
	

	public boolean isIncludeNotEnabled() {
		return includeNotEnabled;
	}

	public void setIncludeNotEnabled(boolean includeNotEnabled) {
		this.includeNotEnabled = includeNotEnabled;
	}

	public boolean isIncludeBloque() {
		return includeBloque;
	}

	public void setIncludeBloque(boolean includeBloque) {
		this.includeBloque = includeBloque;
	}


	
}
