/*
 * Copyright (c) 2015 by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com)
 *
 * traxmol: The stunning micro-library that helps you to develop easily
 *             AJAX web applications by using Java and jQuery
 * traxmol is distributed under the MIT License (MIT)
 * Sources at https://github.com/rafaelaznar/traxmol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.rafaelaznar.dao.genericimplementation;

import eu.rafaelaznar.bean.meta.publicinterface.MetaPropertyBeanInterface;
import eu.rafaelaznar.bean.meta.helper.MetaObjectGenericBeanHelper;
import eu.rafaelaznar.bean.meta.helper.MetaPropertyGenericBeanHelper;
import eu.rafaelaznar.bean.genericimplementation.ViewGenericBeanImplementation;
import eu.rafaelaznar.bean.meta.publicinterface.MetaObjectBeanInterface;
import eu.rafaelaznar.bean.specificimplementation.UsuarioSpecificBeanImplementation;
import eu.rafaelaznar.dao.publicinterface.MetaDaoInterface;
import eu.rafaelaznar.helper.Log4jHelper;
import eu.rafaelaznar.factory.BeanFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import java.sql.Connection;
import java.util.ArrayList;

public abstract class MetaGenericDaoImplementation implements MetaDaoInterface {

    protected String ob = null;
    protected String strSQL = null;

    protected String strCountSQL = null;
    protected Connection oConnection = null;
    protected UsuarioSpecificBeanImplementation oPuserSecurity = null;

    public MetaGenericDaoImplementation(String obj, Connection oPooledConnection, UsuarioSpecificBeanImplementation oPuserBean_security, String strWhere) {
        oConnection = oPooledConnection;
        oPuserSecurity = oPuserBean_security;
        ob = obj;
        strSQL = "select * from " + ob + " WHERE 1=1 ";
        strCountSQL = "select COUNT(*) from " + ob + " WHERE 1=1 ";
        if (strWhere != null) {
            strSQL += strWhere + " ";
            strCountSQL += strWhere + " ";
        }
    }

    private ArrayList<MetaPropertyGenericBeanHelper> fillPropertiesMetaData(Class oClassBEAN, ArrayList<MetaPropertyGenericBeanHelper> alVector) {
        for (Field field : oClassBEAN.getDeclaredFields()) {
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            for (Integer i = 0; i < fieldAnnotations.length; i++) {
                if (fieldAnnotations[i].annotationType().equals(MetaPropertyBeanInterface.class)) {
                    MetaPropertyBeanInterface fieldAnnotation = (MetaPropertyBeanInterface) fieldAnnotations[i];
                    if (!fieldAnnotation.IsIdForeignKey()) {
                        MetaPropertyGenericBeanHelper oMeta = new MetaPropertyGenericBeanHelper();
                        oMeta.setName(fieldAnnotation.Name());
                        oMeta.setShortName(fieldAnnotation.ShortName());
                        oMeta.setLongName(fieldAnnotation.LongName());
                        oMeta.setDescription(fieldAnnotation.Description());
                        oMeta.setIsId(fieldAnnotation.IsId());
                        oMeta.setIsIdForeignKey(fieldAnnotation.IsIdForeignKey());
                        oMeta.setIsObjForeignKey(fieldAnnotation.IsObjForeignKey());
                        oMeta.setReferences(fieldAnnotation.References());
                        oMeta.setIsForeignKeyDescriptor(fieldAnnotation.IsForeignKeyDescriptor());
                        oMeta.setType(fieldAnnotation.Type());
                        oMeta.setIsRequired(fieldAnnotation.IsRequired());
                        oMeta.setRegexPattern(fieldAnnotation.RegexPattern());
                        oMeta.setDefaultValue(fieldAnnotation.DefaultValue());
                        oMeta.setIsVisible(fieldAnnotation.IsVisible());                       
                        alVector.add(oMeta);
                    }
                }
            }
        }
        return alVector;
    }

    private MetaObjectGenericBeanHelper fillObjectMetaData(Class oClassBEAN, MetaObjectGenericBeanHelper oMetaObject) {
        Annotation[] classAnnotations = oClassBEAN.getAnnotations();
        for (Integer i = 0; i < classAnnotations.length; i++) {
            if (classAnnotations[i].annotationType().equals(MetaObjectBeanInterface.class)) {
                MetaObjectBeanInterface fieldAnnotation = (MetaObjectBeanInterface) classAnnotations[i];
                oMetaObject.setName(fieldAnnotation.Name());
                oMetaObject.setDescription(fieldAnnotation.Description());
                oMetaObject.setIcon(fieldAnnotation.Icon());
                oMetaObject.setTableName(fieldAnnotation.TableName());
                oMetaObject.setSqlSelect(fieldAnnotation.SqlSelect());
                oMetaObject.setSqlSelectCount(fieldAnnotation.SqlSelectCount());
                oMetaObject.setType(fieldAnnotation.Type());
            }
        }
        return oMetaObject;
    }

    @Override
    public MetaObjectGenericBeanHelper getObjectMetaData() throws Exception {
        MetaObjectGenericBeanHelper oMetaObject;
        try {
            ViewGenericBeanImplementation oBean = (ViewGenericBeanImplementation) BeanFactory.getBean(ob);
            Class oClassBEAN = oBean.getClass();
            oMetaObject = new MetaObjectGenericBeanHelper();
            oMetaObject = fillObjectMetaData(oClassBEAN, oMetaObject);
        } catch (Exception ex) {
            String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
            Log4jHelper.errorLog(msg, ex);
            throw new Exception(msg, ex);
        }
        return oMetaObject;
    }

    @Override
    public ArrayList<MetaPropertyGenericBeanHelper> getPropertiesMetaData() throws Exception {
        ArrayList<MetaPropertyGenericBeanHelper> alVector = new ArrayList<>();
        try {
            ViewGenericBeanImplementation oBean = (ViewGenericBeanImplementation) BeanFactory.getBean(ob);
            Class classBean = oBean.getClass();
            Class superClassBean = oBean.getClass().getSuperclass();
            alVector = fillPropertiesMetaData(classBean, alVector);
            alVector = fillPropertiesMetaData(superClassBean, alVector);
        } catch (Exception ex) {
            String msg = this.getClass().getName() + ":" + (ex.getStackTrace()[0]).getMethodName();
            Log4jHelper.errorLog(msg, ex);
            throw new Exception(msg, ex);
        }
        return alVector;
    }

}
