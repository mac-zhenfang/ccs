package com.cisco.css;

public class ErrorCode {

	/*********** Common ************/
	public static final String COMMON_NOT_APP_MEMBER = "0000";
	public static final String COMMON_JSON_PARSE_ERROR = "0001";
	public static final String COMMON_NO_USER_TOKEN = "0002";
	public static final String COMMON_ILLEGAL_STRING = "0003";
	public static final String COMMON_DATEFORMAT_ILLEGAL = "0004";
	public static final String COMMON_ENTRY_NOT_EXISTED = "0005";
	public static final String COMMON_CONFIG_LOAD_FAIL = "0006";
	public static final String COMMON_HDFS_ERROR = "0007";
	public static final String COMMON_LENGTH_EXCEED = "0008";
	public static final String COMMON_EMPTY_STRING = "0010";
	public static final String COMMON_NOT_REGEX = "0011";
	public static final String COMMON_INVALID_APPTOKEN = "0012";
	public static final String COMMON_ENTRY_ALREADY_EXISTED = "0013";
	public static final String COMMON_UNSUPPORT_ERROR = "0998";
	public static final String COMMON_UNDEFINED_ERROR = "0999";

	/*********** App ************/
	public static final String APP_NOT_ADMIN = "1000";
	public static final String APP_MEMBER_REMOVE_SELF = "1001";
	public static final String APP_MEMBER_UPDATE_SELF = "1002";



	/*********** Table ************/
	public static final String TABLE_EMPTY_SCHEMA = "3001";
	public static final String TABLE_NOT_SUPPORTED_LINEDELIMITER = "3003";
	public static final String TABLE_SHARED_APPS_CONFLICT = "3004";
	public static final String TABLE_NOT_SUPPORTED_FORMAT_TYPE = "3005";
	public static final String TABLE_FAIL_TO_READ = "3006";
	public static final String TABLE_HIVE_EXCUTER_FAIL = "3007";
	public static final String TABLE_CREATE_ILLEGAL_DATA_TYPE = "3008";
	public static final String TABLE_SERDENAME_NOT_EXIST = "3009";
	public static final String TABLE_RECURSIVELY_SHARE = "3010";
	public static final String TABLE_FIELD_DELIMITER_CHAR = "3011";
	public static final String UNSUPPORTED_SERDE_NAME = "3012";
	public static final String INAPRROPRIATE_SERDE_PROPERTIES = "3013";
	public static final String REGEXSERDE_ONLY_ACCEPT_STRING_COLUMNS = "3014";

	/*********** MODEL ************/
	public static final String MODEL_TABLE_IN_OTHER_APP = "4001";
	public static final String MODEL_RAWDATA_TABLE_USED = "4002";
	public static final String MODEL_TABLE_USED_IN_OTHER_MODEL = "4003";
	public static final String MODEL_INVALID_PERIOD = "4004";
	public static final String MODEL_ALREADY_DELETED = "4005";
	public static final String MODEL_SCHEDULE_FAIL = "4006";
	public static final String MODEL_RESCHEDULE_FAIL = "4007";
	public static final String MODEL_CHANGE_SCHEDULE_STATUS_FAIL = "4008";
	public static final String MODEL_FIND_SCHEMA_FAIL = "4009";
	public static final String MODEL_SHARED_TABLE_USED = "4010";
	public static final String MODEL_SQL_ILLEGAL = "4011";
	public static final String MODEL_COLUMN_NUMBER_NOT_MATCH_OUTTABLE = "4012";
	public static final String MODEL_FAIL_TO_EXECUTE = "4013";

	/*********** Report ************/
	public static final String REPORT_BELONG_APP_ILLEGAL = "5001";
	public static final String REPORT_CATEGORY_ILLEGAL = "5002";
	public static final String REPORT_RESULT_NOT_FOUND = "5003";
	public static final String REPORT_VISUALDEF_REPORT_TYPE_ILLEGAL = "5004";
	public static final String REPORT_VISUALDEF_NULL = "5005";
	public static final String REPORT_RESULTSET_TOO_LARGE = "5006";
	public static final String REPORT_INVALID_MODEL_LOGIC = "5007";
	public static final String REPORT_WRONG_DATA_FORMAT = "5008";
}
