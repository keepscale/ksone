package org.crossfit.app.i18n.domain.web;

import org.crossfit.app.i18n.domain.I18nNameKey;

public enum I18NameKeyEnum implements I18nNameKey{
	META_DATA_TITLE("title");

	final String name;
	
	I18NameKeyEnum(String name) {
		this.name = name;
	}
	@Override
	public String getI18nName() {
		return name;
	}

}
