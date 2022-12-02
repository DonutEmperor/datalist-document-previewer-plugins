/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.joget.marketplace;
import com.google.common.io.Files;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.apps.datalist.service.DataListService;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.workflow.util.WorkflowUtil;
/**
 *
 * @author User
 */
public class WordViewer extends DataListColumnFormatDefault{
    private final static String MESSAGE_PATH = "message/form/WordViewer";
    
    @Override
    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
        String result = (String)value;
  
        if (result != null && !result.isEmpty()) {
            try {
                String formDefId = getPropertyString("formDefId");
                AppDefinition appDef = AppUtil.getCurrentAppDefinition();
                result = "";
  
                String attachment = "";
                if ("true".equals(getPropertyString("attachment"))) {
                    attachment = "?attachment=true";
                }
  
                String primaryKeyValue = DataListService.evaluateColumnValueFromRow(row, dataList.getBinder().getPrimaryKeyColumnName()).toString();
                 
                HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
  
                //suport for multi values
                for (String v : value.toString().split(";")) {
                    if (!v.isEmpty()) {
                        // determine actual path for the file uploads
                        String fileName = v;
                        String encodedFileName = fileName;
  
                        try {
                            encodedFileName = URLEncoder.encode(fileName, "UTF8").replaceAll("\\+", "%20");
                        } catch (UnsupportedEncodingException ex) {
                            // ignore
                        }
                        
                        String filePath = request.getContextPath() + "/web/client/app/" + appDef.getAppId() + "/" + appDef.getVersion().toString() + "/form/download/" + formDefId + "/" + primaryKeyValue + "/" + encodedFileName + "." + attachment;
  
                        if (!result.isEmpty()) {
                            result += ", ";
                        }
                        else if(getFileExtension(encodedFileName).equals("docx") || getFileExtension(encodedFileName).equals("doc")){
                            result += "<a href=\""+ "https://view.officeapps.live.com/op/embed.aspx?src=https://" + request.getServerName() + ":" + request.getServerPort() + filePath+"\" target=\"_blank\">"+ StringUtil.stripAllHtmlTag(fileName)+"</a>";
                        }else{
                            result += "<a href=\""+filePath+"\" target=\"_blank\">"+StringUtil.stripAllHtmlTag(fileName)+"</a>";
                        }
                        
                    }
                }
            } catch (Exception e) {
                LogUtil.error(getClassName(), e, "");
            }
        }
        return result;
        
    } 

    @Override
    public String getName() {
        return "Word Viewer Datalist Action";
    }

    @Override
    public String getVersion() {
        return "8.0.0";
    }

    @Override
    public String getDescription() {
        return AppPluginUtil.getMessage("org.joget.marketplace.WordViewer.pluginDesc", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getLabel() {
        return AppPluginUtil.getMessage("org.joget.marketplace.WordViewer.pluginLabel", getClassName(), MESSAGE_PATH);
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/form/wordViewer.json", null, true, MESSAGE_PATH);
    }
    
    //method to get file extension
    public String getFileExtension(String fullName) {
    String extension = "";
    int i = fullName.lastIndexOf('.');
    if (i >= 0) { extension = fullName.substring(i+1); }
    
    return extension;
    }
}
