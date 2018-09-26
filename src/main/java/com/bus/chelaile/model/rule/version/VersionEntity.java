package com.bus.chelaile.model.rule.version;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 该类是一个不可变类， 一旦实例构造成功， 将不能再修改它的值。
 * @author liujh
 *
 */
public class VersionEntity {
    private static final Logger logger = LoggerFactory.getLogger(VersionEntity.class);
    
    public static final VersionEntity NULL_OBJECT = new VersionEntity(0, 0, 0);
    
    private int major;  // 主版本号
    private int minor;  // 次版本号
    private int revision; //修订版本号
    
    public VersionEntity() {
        major = 0;
        minor = 0;
        revision = 0;
    }
    
    public VersionEntity(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }
    
    public String toString() {
        return major + "." + minor + "." + revision;
    }
    
    public int compareTo(VersionEntity other) {
        if (other == null) {
            return 1;
        }

        if (this.major != other.major) {
            return this.major - other.major;
        }

        if (this.minor != other.minor) {
            return this.minor - other.minor;
        }

        return this.revision - other.revision;
    }
    
    public static VersionEntity parseVersionStr(String inputStr) {
        String versionStr = StringUtils.trim(inputStr);
        
        if (versionStr == null || versionStr.isEmpty()) {
            return null;
        }
        
        try {
            int major = 0;
            int minor = 0;
            int revision = 0;

            int firstDotIdx = versionStr.indexOf('.');
            if (firstDotIdx < 0) {
//                logger.error("[ERROR_VERSION] 无法解析版本号：versionStr={}", inputStr);
                major = Integer.valueOf(versionStr);
            } else {
                major = Integer.valueOf(versionStr.substring(0, firstDotIdx));
                
                int secondDotIdx = versionStr.indexOf('.', firstDotIdx + 1);
                if (secondDotIdx > 0) {
                    minor = Integer.valueOf(versionStr.substring(firstDotIdx + 1, secondDotIdx));
                    
                    int testSlash = versionStr.indexOf('_', secondDotIdx + 1);
                    if (testSlash > 0) {
                        revision = Integer.valueOf(versionStr.substring(secondDotIdx + 1, testSlash));
                    } else {
                        revision = Integer.valueOf(versionStr.substring(secondDotIdx + 1));
                    }
                } else {
                    minor = Integer.valueOf(versionStr.substring(firstDotIdx + 1));
                }
            }
            
            return new VersionEntity(major, minor, revision);
        } catch (NumberFormatException nfe) {
            //nfe.printStackTrace();
            logger.error("parseVersionStr ", nfe.getMessage());
            logger.error("[ERROR_VERSION] 错误版本号: versionStr={}", inputStr);
        } catch (Exception ex) {
            //ex.printStackTrace();
            logger.error("parseVersionStr ex:", ex.getMessage());
            logger.error(String.format("[ERROR_VERSION] 解析版本异常: versionStr=%s, errMsg=%s", inputStr, ex.getMessage()), ex);
        }
        
        return null;
    }
    
    public int hashCode()
	{
		int PRIME = 59;
		int result = 1;
		result = result * PRIME + getMajor();
		result = result * PRIME + getMinor();
		result = result * PRIME + getRevision();
		return result;
	}
    
    public boolean equals(Object o)
	{
		if (o == this) {
			return true;
		}
		if (!(o instanceof VersionEntity)) {
			return false;
		}
		VersionEntity other = (VersionEntity)o;
		if (!other.canEqual(this)) {
			return false;
		}
		if (getMajor() != other.getMajor()) {
			return false;
		}
		if (getMinor() != other.getMinor()) {
			return false;
		}
		return getRevision() == other.getRevision();
	}

	private boolean canEqual(Object other)
	{
		return other instanceof VersionEntity;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static VersionEntity getNullObject() {
		return NULL_OBJECT;
	}
    
	
	public static void main(String[] args) {
	    String v = "7";
	    String v1 = "7.0";
	    String v2 = "6.3.4";
	    String v3 = "10";
	    
	    long t1 = System.currentTimeMillis();
	    for(int i = 0; i < 100000; i ++) {
	    VersionEntity ve = parseVersionStr(v);
	    VersionEntity ve1 = parseVersionStr(v1);
	    VersionEntity ve2 = parseVersionStr(v2);
	    VersionEntity ve3 = parseVersionStr(v3);
	    }
	    System.out.println(System.currentTimeMillis() - t1);
	    
	}
}
