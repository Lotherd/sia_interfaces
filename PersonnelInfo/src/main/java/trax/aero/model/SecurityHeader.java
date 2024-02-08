package trax.aero.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the SECURITY_HEADER database table.
 * 
 */
@Entity
@Table(name="SECURITY_HEADER")
@NamedQuery(name="SecurityHeader.findAll", query="SELECT s FROM SecurityHeader s")
public class SecurityHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"USER\"")
	private String user;

	@Column(name="ADOPT_USER_PROFILE")
	private String adoptUserProfile;

	@Column(name="ALLOW_PASSWORD_CHANGE_BY_USER")
	private String allowPasswordChangeByUser;

	@Column(name="AUTO_GENERATE_WALL_PASS")
	private String autoGenerateWallPass;

	@Column(name="BG_COLOR_PREFERENCE")
	private BigDecimal bgColorPreference;

	@Column(name="CATEGORY_TITLE")
	private String categoryTitle;

	@Column(name="CLICK_ITEMS")
	private String clickItems;

	@Column(name="COMPANY_PROFILE")
	private String companyProfile;

	@Column(name="COST_CENTER")
	private String costCenter;

	@Column(name="CREATED_BY")
	private String createdBy;

	@Column(name="CREATED_DATE")
	private Date createdDate;

	private String customer;

	@Column(name="CUSTOMER_SECURITY")
	private String customerSecurity;

	@Column(name="DATAIWNDOW_COLOR")
	private BigDecimal dataiwndowColor;

	private String department;

	private String description;

	@Column(name="\"DICTIONARY\"")
	private String dictionary;

	private String employee;

	private String explorer;

	@Column(name="EXPLORER_ICON")
	private String explorerIcon;

	@Column(name="FIELD_HIGHTLIGHT")
	private String fieldHightlight;

	@Column(name="FIELD_STYLE")
	private BigDecimal fieldStyle;

	@Column(name="FILTER_SORT_REQ_TRANS")
	private String filterSortReqTrans;

	@Column(name="FILTER_SORT_TRANS")
	private String filterSortTrans;

	@Column(name="FIRST_TIME_PASS_CONTROL")
	private String firstTimePassControl;

	@Column(name="FIRST_TIME_WALL_CONTROL")
	private String firstTimeWallControl;

	@Column(name="GRADIENT_COLOR")
	private String gradientColor;

	@Column(name="GRADIENT_INVERSE")
	private String gradientInverse;

	@Column(name="GRADIENT_OFF")
	private String gradientOff;

	private BigDecimal height;

	@Column(name="INVENTORY_FLAG")
	private String inventoryFlag;

	@Column(name="LAST_SINGOFF")
	private Date lastSingoff;

	@Column(name="LAST_SINGON")
	private Date lastSingon;

	private String location;

	@Column(name="MODIFIED_BY")
	private String modifiedBy;

	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name="NAV_PANEL_LAST")
	private String navPanelLast;

	private BigDecimal notes;

	private String password;

	@Column(name="PASSWORD_RECOVERY")
	private String passwordRecovery;

	@Column(name="POP_MESSAGE")
	private String popMessage;

	@Column(name="PRODCTRL_SINGON")
	private Date prodctrlSingon;

	@Column(name="RECENT_NO")
	private BigDecimal recentNo;

	@Column(name="RESET_CODE")
	private String resetCode;

	@Column(name="ROUTING_BEGIN_DATE")
	private Date routingBeginDate;

	@Column(name="ROUTING_EXPIRATION_DATE")
	private Date routingExpirationDate;

	@Column(name="ROUTING_STATUS")
	private String routingStatus;

	@Column(name="ROW_INDICATOR")
	private BigDecimal rowIndicator;

	private String security;

	@Column(name="SECURITY_EXPIRATION_DATE")
	private Date securityExpirationDate;

	@Column(name="SECURITY_EXPIRATION_DAYS")
	private BigDecimal securityExpirationDays;

	@Column(name="SECURITY_EXPIRATION_DAYS_WALL")
	private BigDecimal securityExpirationDaysWall;

	@Column(name="SELBOX_FAV")
	private BigDecimal selboxFav;

	@Column(name="SESSION_GROUP")
	private String sessionGroup;

	@Column(name="SESSION_USERNAME")
	private String sessionUsername;

	private String shortcutbar;

	private String status;

	@Column(name="STYLESHEET_EDITOR")
	private String stylesheetEditor;

	@Column(name="STYLESHEET_EDITOR_HIDE_RTF")
	private String stylesheetEditorHideRtf;

	@Column(name="TAB_CONTROL")
	private String tabControl;

	private String tabposition;

	@Column(name="TOOL_BAR_SHOW_TEXT")
	private String toolBarShowText;

	@Column(name="TOOLBAR_LOCATION")
	private String toolbarLocation;

	@Column(name="TRAX_ONLY_MYPROFILE")
	private String traxOnlyMyprofile;

	@Column(name="TRAXDOC_EXPLORER_VIEW")
	private String traxdocExplorerView;

	@Column(name="TURN_OFF_TRANSPARENCY")
	private String turnOffTransparency;

	@Column(name="USER_PROFILE")
	private String userProfile;

	@Column(name="VIEW_TYPE")
	private String viewType;

	@Column(name="WALL_PASSWORD_CHANGE_BY_USER")
	private String wallPasswordChangeByUser;

	private BigDecimal width;

	@Column(name="ZOOM_PRECENT")
	private BigDecimal zoomPrecent;

	public SecurityHeader() {
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAdoptUserProfile() {
		return this.adoptUserProfile;
	}

	public void setAdoptUserProfile(String adoptUserProfile) {
		this.adoptUserProfile = adoptUserProfile;
	}

	public String getAllowPasswordChangeByUser() {
		return this.allowPasswordChangeByUser;
	}

	public void setAllowPasswordChangeByUser(String allowPasswordChangeByUser) {
		this.allowPasswordChangeByUser = allowPasswordChangeByUser;
	}

	public String getAutoGenerateWallPass() {
		return this.autoGenerateWallPass;
	}

	public void setAutoGenerateWallPass(String autoGenerateWallPass) {
		this.autoGenerateWallPass = autoGenerateWallPass;
	}

	public BigDecimal getBgColorPreference() {
		return this.bgColorPreference;
	}

	public void setBgColorPreference(BigDecimal bgColorPreference) {
		this.bgColorPreference = bgColorPreference;
	}

	public String getCategoryTitle() {
		return this.categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public String getClickItems() {
		return this.clickItems;
	}

	public void setClickItems(String clickItems) {
		this.clickItems = clickItems;
	}

	public String getCompanyProfile() {
		return this.companyProfile;
	}

	public void setCompanyProfile(String companyProfile) {
		this.companyProfile = companyProfile;
	}

	public String getCostCenter() {
		return this.costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCustomer() {
		return this.customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerSecurity() {
		return this.customerSecurity;
	}

	public void setCustomerSecurity(String customerSecurity) {
		this.customerSecurity = customerSecurity;
	}

	public BigDecimal getDataiwndowColor() {
		return this.dataiwndowColor;
	}

	public void setDataiwndowColor(BigDecimal dataiwndowColor) {
		this.dataiwndowColor = dataiwndowColor;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDictionary() {
		return this.dictionary;
	}

	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}

	public String getEmployee() {
		return this.employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getExplorer() {
		return this.explorer;
	}

	public void setExplorer(String explorer) {
		this.explorer = explorer;
	}

	public String getExplorerIcon() {
		return this.explorerIcon;
	}

	public void setExplorerIcon(String explorerIcon) {
		this.explorerIcon = explorerIcon;
	}

	public String getFieldHightlight() {
		return this.fieldHightlight;
	}

	public void setFieldHightlight(String fieldHightlight) {
		this.fieldHightlight = fieldHightlight;
	}

	public BigDecimal getFieldStyle() {
		return this.fieldStyle;
	}

	public void setFieldStyle(BigDecimal fieldStyle) {
		this.fieldStyle = fieldStyle;
	}

	public String getFilterSortReqTrans() {
		return this.filterSortReqTrans;
	}

	public void setFilterSortReqTrans(String filterSortReqTrans) {
		this.filterSortReqTrans = filterSortReqTrans;
	}

	public String getFilterSortTrans() {
		return this.filterSortTrans;
	}

	public void setFilterSortTrans(String filterSortTrans) {
		this.filterSortTrans = filterSortTrans;
	}

	public String getFirstTimePassControl() {
		return this.firstTimePassControl;
	}

	public void setFirstTimePassControl(String firstTimePassControl) {
		this.firstTimePassControl = firstTimePassControl;
	}

	public String getFirstTimeWallControl() {
		return this.firstTimeWallControl;
	}

	public void setFirstTimeWallControl(String firstTimeWallControl) {
		this.firstTimeWallControl = firstTimeWallControl;
	}

	public String getGradientColor() {
		return this.gradientColor;
	}

	public void setGradientColor(String gradientColor) {
		this.gradientColor = gradientColor;
	}

	public String getGradientInverse() {
		return this.gradientInverse;
	}

	public void setGradientInverse(String gradientInverse) {
		this.gradientInverse = gradientInverse;
	}

	public String getGradientOff() {
		return this.gradientOff;
	}

	public void setGradientOff(String gradientOff) {
		this.gradientOff = gradientOff;
	}

	public BigDecimal getHeight() {
		return this.height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public String getInventoryFlag() {
		return this.inventoryFlag;
	}

	public void setInventoryFlag(String inventoryFlag) {
		this.inventoryFlag = inventoryFlag;
	}

	public Date getLastSingoff() {
		return this.lastSingoff;
	}

	public void setLastSingoff(Date lastSingoff) {
		this.lastSingoff = lastSingoff;
	}

	public Date getLastSingon() {
		return this.lastSingon;
	}

	public void setLastSingon(Date lastSingon) {
		this.lastSingon = lastSingon;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getNavPanelLast() {
		return this.navPanelLast;
	}

	public void setNavPanelLast(String navPanelLast) {
		this.navPanelLast = navPanelLast;
	}

	public BigDecimal getNotes() {
		return this.notes;
	}

	public void setNotes(BigDecimal notes) {
		this.notes = notes;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRecovery() {
		return this.passwordRecovery;
	}

	public void setPasswordRecovery(String passwordRecovery) {
		this.passwordRecovery = passwordRecovery;
	}

	public String getPopMessage() {
		return this.popMessage;
	}

	public void setPopMessage(String popMessage) {
		this.popMessage = popMessage;
	}

	public Date getProdctrlSingon() {
		return this.prodctrlSingon;
	}

	public void setProdctrlSingon(Date prodctrlSingon) {
		this.prodctrlSingon = prodctrlSingon;
	}

	public BigDecimal getRecentNo() {
		return this.recentNo;
	}

	public void setRecentNo(BigDecimal recentNo) {
		this.recentNo = recentNo;
	}

	public String getResetCode() {
		return this.resetCode;
	}

	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}

	public Date getRoutingBeginDate() {
		return this.routingBeginDate;
	}

	public void setRoutingBeginDate(Date routingBeginDate) {
		this.routingBeginDate = routingBeginDate;
	}

	public Date getRoutingExpirationDate() {
		return this.routingExpirationDate;
	}

	public void setRoutingExpirationDate(Date routingExpirationDate) {
		this.routingExpirationDate = routingExpirationDate;
	}

	public String getRoutingStatus() {
		return this.routingStatus;
	}

	public void setRoutingStatus(String routingStatus) {
		this.routingStatus = routingStatus;
	}

	public BigDecimal getRowIndicator() {
		return this.rowIndicator;
	}

	public void setRowIndicator(BigDecimal rowIndicator) {
		this.rowIndicator = rowIndicator;
	}

	public String getSecurity() {
		return this.security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public Date getSecurityExpirationDate() {
		return this.securityExpirationDate;
	}

	public void setSecurityExpirationDate(Date securityExpirationDate) {
		this.securityExpirationDate = securityExpirationDate;
	}

	public BigDecimal getSecurityExpirationDays() {
		return this.securityExpirationDays;
	}

	public void setSecurityExpirationDays(BigDecimal securityExpirationDays) {
		this.securityExpirationDays = securityExpirationDays;
	}

	public BigDecimal getSecurityExpirationDaysWall() {
		return this.securityExpirationDaysWall;
	}

	public void setSecurityExpirationDaysWall(BigDecimal securityExpirationDaysWall) {
		this.securityExpirationDaysWall = securityExpirationDaysWall;
	}

	public BigDecimal getSelboxFav() {
		return this.selboxFav;
	}

	public void setSelboxFav(BigDecimal selboxFav) {
		this.selboxFav = selboxFav;
	}

	public String getSessionGroup() {
		return this.sessionGroup;
	}

	public void setSessionGroup(String sessionGroup) {
		this.sessionGroup = sessionGroup;
	}

	public String getSessionUsername() {
		return this.sessionUsername;
	}

	public void setSessionUsername(String sessionUsername) {
		this.sessionUsername = sessionUsername;
	}

	public String getShortcutbar() {
		return this.shortcutbar;
	}

	public void setShortcutbar(String shortcutbar) {
		this.shortcutbar = shortcutbar;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStylesheetEditor() {
		return this.stylesheetEditor;
	}

	public void setStylesheetEditor(String stylesheetEditor) {
		this.stylesheetEditor = stylesheetEditor;
	}

	public String getStylesheetEditorHideRtf() {
		return this.stylesheetEditorHideRtf;
	}

	public void setStylesheetEditorHideRtf(String stylesheetEditorHideRtf) {
		this.stylesheetEditorHideRtf = stylesheetEditorHideRtf;
	}

	public String getTabControl() {
		return this.tabControl;
	}

	public void setTabControl(String tabControl) {
		this.tabControl = tabControl;
	}

	public String getTabposition() {
		return this.tabposition;
	}

	public void setTabposition(String tabposition) {
		this.tabposition = tabposition;
	}

	public String getToolBarShowText() {
		return this.toolBarShowText;
	}

	public void setToolBarShowText(String toolBarShowText) {
		this.toolBarShowText = toolBarShowText;
	}

	public String getToolbarLocation() {
		return this.toolbarLocation;
	}

	public void setToolbarLocation(String toolbarLocation) {
		this.toolbarLocation = toolbarLocation;
	}

	public String getTraxOnlyMyprofile() {
		return this.traxOnlyMyprofile;
	}

	public void setTraxOnlyMyprofile(String traxOnlyMyprofile) {
		this.traxOnlyMyprofile = traxOnlyMyprofile;
	}

	public String getTraxdocExplorerView() {
		return this.traxdocExplorerView;
	}

	public void setTraxdocExplorerView(String traxdocExplorerView) {
		this.traxdocExplorerView = traxdocExplorerView;
	}

	public String getTurnOffTransparency() {
		return this.turnOffTransparency;
	}

	public void setTurnOffTransparency(String turnOffTransparency) {
		this.turnOffTransparency = turnOffTransparency;
	}

	public String getUserProfile() {
		return this.userProfile;
	}

	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}

	public String getViewType() {
		return this.viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public String getWallPasswordChangeByUser() {
		return this.wallPasswordChangeByUser;
	}

	public void setWallPasswordChangeByUser(String wallPasswordChangeByUser) {
		this.wallPasswordChangeByUser = wallPasswordChangeByUser;
	}

	public BigDecimal getWidth() {
		return this.width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getZoomPrecent() {
		return this.zoomPrecent;
	}

	public void setZoomPrecent(BigDecimal zoomPrecent) {
		this.zoomPrecent = zoomPrecent;
	}

}