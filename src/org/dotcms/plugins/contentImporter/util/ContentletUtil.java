package org.dotcms.plugins.contentImporter.util;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.dotcms.repackage.com.csvreader.CsvReader;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.languagesmanager.business.LanguageAPI;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.tag.business.TagAPI;
import com.dotmarketing.util.ImportUtil;
import com.liferay.portal.model.User;

public class ContentletUtil {

	//API
	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();
	private static ContentletAPI conAPI = APILocator.getContentletAPI();
	private static CategoryAPI catAPI = APILocator.getCategoryAPI();
	private static LanguageAPI langAPI = APILocator.getLanguageAPI();
	private static TagAPI tagAPI = APILocator.getTagAPI();
	private static HostAPI hostAPI = APILocator.getHostAPI();
	private static FolderAPI folderAPI = APILocator.getFolderAPI();

	//Final Variables
	public final static String languageCodeHeader = "languageCode";
	public final static String countryCodeHeader = "countryCode";
	public final static String identifierFieldName = "identifier";
	private int identifierFieldPosition = -1;
	private int languageFieldPosition = -1;
	private int countryFieldPosition = -1;
	private User systemUser = null;
	private Host defaultHost = null;

	//Temp maps used to parse the file
	private HashMap<Integer, Field> headers;
	private HashMap<Integer, Field> keyFields;

	//Counters for the preview page 
	private int newContentCounter;
	private int contentToUpdateCounter;

	//Counters for the results page
	private int contentUpdatedDuplicated;
	private int contentUpdated;
	private int contentCreated;
	private HashSet keyContentUpdated = new HashSet(); 
	private StringBuffer choosenKeyField;

	private int commitGranularity = 10;
	private int sleepTime = 200;
	private Role CMSAdmin; 
	private List<Permission> structurePermissions;

	//Field from the Action
	private Reader reader;
	private CsvReader csvreader;
	private String[] csvHeaders;	

	static int languageCodeHeaderColumn = -1;
	static int countryCodeHeaderColumn = -1;

	private static final SimpleDateFormat DATE_FIELD_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static final String DOTSCHEDULER_DATE = "EEE MMM d hh:mm:ss z yyyy";

	public ContentletUtil(Reader reader, CsvReader csvreader)
	{
		try
		{
			this.reader = reader;
			this.csvreader = csvreader;
			this.csvHeaders = csvreader.getHeaders();
			this.systemUser = APILocator.getUserAPI().getSystemUser();
			this.defaultHost = WebAPILocator.getHostWebAPI().findDefaultHost(systemUser, false);
		}
		catch(Exception ex)
		{
			throw new DotRuntimeException(ex.getMessage(),ex);
		}
	}

	public HashMap<String, List<String>> importFile(String structure, String[] keyfields, boolean preview,User user,boolean isMultilingual, long language,boolean publishContent, boolean saveWithoutVersions)
			throws DotRuntimeException, DotDataException 
			{
	    
	    
	   return ImportUtil.importFile(System.currentTimeMillis(), defaultHost.getIdentifier(), structure, keyfields, preview, isMultilingual, user, language, keyfields, csvreader, languageCodeHeaderColumn, countryCodeHeaderColumn, reader);
	    
	    
	}



	/**
	 * This method drop all the content associated to this structure
	 * @param struture structure ID
	 * @param user User with permission
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public void deleteAllContent(String struture, User user) throws DotSecurityException, DotDataException{
		int limit = 200;
		int offset = 0;
		ContentletAPI conAPI=APILocator.getContentletAPI();
		List<Contentlet> contentlets=null;
		Structure st = CacheLocator.getContentTypeCache().getStructureByInode (struture);
		do {
			contentlets = conAPI.findByStructure(st, user, false, limit, offset);
			conAPI.delete(contentlets, user, false);
		} while(contentlets.size()>0);
		conAPI.refresh(st);
	}



}
