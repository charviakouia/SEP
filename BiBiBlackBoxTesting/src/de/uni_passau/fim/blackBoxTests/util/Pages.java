package de.uni_passau.fim.blackBoxTests.util;

public enum Pages {

    HOME("", null),
    SITE_NOTICE("view/ffa/site-notice.xhtml", "site_notice_output_text"),
    MEDIUM_SEARCH("view/opac/medium-search.xhtml", "form_medium_search"),
    CATEGORIES("view/opac/category-browser.xhtml", null),
    SIGN_IN("view/ffa/login.xhtml", null),
    REGISTER("view/ffa/registration.xhtml", "registrationForm"),
    PROFILE(null, "form_profile");

    private final String contextUrl;
    private final String uniqueElementId;

    Pages(String contextUrl, String uniqueElementId){
        this.contextUrl = contextUrl;
        this.uniqueElementId = uniqueElementId;
    }

    public String getContextUrl() {
        return contextUrl;
    }

    public String getUniqueElementId(){ return uniqueElementId; }

}
