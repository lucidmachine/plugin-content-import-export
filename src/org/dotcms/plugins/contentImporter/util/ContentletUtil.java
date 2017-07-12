package org.dotcms.plugins.contentImporter.util;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import com.dotcms.repackage.com.csvreader.CsvReader;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.ImportUtil;
import com.liferay.portal.model.User;

public class ContentletUtil {

    // API

    // Final Variables
    public final static String languageCodeHeader = "languageCode";
    public final static String countryCodeHeader = "countryCode";
    public final static String identifierFieldName = "identifier";

    private User systemUser = null;
    private Host defaultHost = null;

    


    // Field from the Action
    private Reader reader;
    private CsvReader csvreader;

    static int languageCodeHeaderColumn = -1;
    static int countryCodeHeaderColumn = -1;


    public static final String DOTSCHEDULER_DATE = "EEE MMM d hh:mm:ss z yyyy";

    public ContentletUtil(Reader reader, CsvReader csvreader) {
        try {
            this.reader = reader;
            this.csvreader = csvreader;

            this.systemUser = APILocator.systemUser();
            this.defaultHost = WebAPILocator.getHostWebAPI().findDefaultHost(systemUser, false);
        } catch (Exception ex) {
            throw new DotRuntimeException(ex.getMessage(), ex);
        }
    }

    public HashMap<String, List<String>> importFile(String structure, String[] keyfields,
                    boolean preview, User user, boolean isMultilingual, long language,
                    boolean publishContent, boolean saveWithoutVersions)
                    throws DotRuntimeException, DotDataException {

        
        
        

        return ImportUtil.importFile(System.currentTimeMillis(), defaultHost.getIdentifier(),
                        structure, keyfields, preview, isMultilingual, user, language, keyfields,
                        csvreader, languageCodeHeaderColumn, countryCodeHeaderColumn, reader);


    }

    public void deleteAllContent(String struture, User user)
                    throws DotSecurityException, DotDataException {
        int limit = 200;
        int offset = 0;
        ContentletAPI conAPI = APILocator.getContentletAPI();
        List<Contentlet> contentlets = null;
        Structure st = CacheLocator.getContentTypeCache().getStructureByInode(struture);
        do {
            contentlets = conAPI.findByStructure(st, user, false, limit, offset);
            conAPI.delete(contentlets, user, false);
        } while (contentlets.size() > 0);
        conAPI.refresh(st);
    }



}
