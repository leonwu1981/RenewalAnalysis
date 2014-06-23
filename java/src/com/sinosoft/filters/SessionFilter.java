package com.sinosoft.filters;


import java.util.ArrayList;


import javax.servlet.*;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;


import com.sinosoft.lis.pubfun.GlobalInput;

import com.sinosoft.lis.pubfun.PubFun;

import com.sinosoft.utility.SSRS;

import com.sinosoft.utility.ExeSQL;


public class SessionFilter implements Filter

{


    public SessionFilter()

    {}

    public String indexpagename = "../indexlis.jsp";


    private String[] excludePages = null;


    public void init(FilterConfig filterConfig) throws ServletException

    {

        String strExcludePageNum = filterConfig.getInitParameter(
                "excludePageNum");

        if (strExcludePageNum != null)

        {

            try

            {

                Integer excludePageNum = Integer.valueOf(strExcludePageNum);

                ArrayList list = new ArrayList();

                for (int i = 0; i < excludePageNum.intValue(); i++)

                {

                    String strPage = filterConfig.getInitParameter("page" + i);

                    if (strPage != null)

                    {

                        list.add(strPage);

                    }

                    else

                    {

                        break;

                    }

                }

                if (list.size() > 0)

                {

                    excludePages = new String[list.size()];

                    excludePages = (String[]) list.toArray(excludePages);

                }

            }

            catch (NumberFormatException ex)

            {

                ex.printStackTrace();

            }

        }

    }


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)

            throws java.io.IOException, ServletException

    {

        HttpSession session = ((HttpServletRequest) request).getSession();

        GlobalInput tGI = (GlobalInput) session.getAttribute("GI");

        HttpServletRequest req = (HttpServletRequest) request;

        String servletPath = req.getServletPath();
        String servletPathBak = servletPath;
        String dirPath = "";
        if (servletPath.length() > 9) {
            dirPath = servletPath.substring(1, 9);
//            System.out.println("dirPath:" + dirPath);
        }

        
        if(servletPath.indexOf("zh-cn")!=-1 || servletPath.indexOf("en")!=-1){
            servletPath = servletPath.substring(servletPath.indexOf("/") + 1);
            if (servletPath.indexOf("/") != -1) {
                servletPath = servletPath.substring(servletPath.indexOf("/"));
            }
        }

        if (!(servletPath.equals("/indexlis.jsp") || servletPath.equals("/logon/menu.jsp") ||
              servletPath.equals("/logon/menu.jsp")|| dirPath.equals("easyscan")) &&
            (servletPath.indexOf("zh-cn")!=-1 || servletPath.indexOf("en")!=-1) )
        {

            if (tGI == null && !isExcludePage(servletPathBak) )

            {

                System.out.println("session is null");
//                System.out.println("innnnnnnn");
                System.out.println("dirPath****:" + servletPath);
                HttpServletResponse hres = (HttpServletResponse) response;

                hres.sendRedirect(indexpagename);

            }

            else

            {

                ExeSQL tExeSQL = new ExeSQL();

                String searchSql =
                        "select count(1) from ldmenu where NodeSign = '2' and runscript like '%" +
                        servletPathBak + "%'";

                SSRS tSSRS = tExeSQL.execSQL(searchSql);

                if (tGI != null && tSSRS != null)

                {

                    String tt[] = tSSRS.getRowData(1);

                    if (!tt[0].equals("0"))

                    {

                        if (!PubFun.canIDo(tGI, ".."+servletPathBak, "page"))

                        {
                        	System.out.println("dirPath****:" + servletPath);
                            HttpServletResponse hres = (HttpServletResponse)
                                    response;

                            hres.sendRedirect(indexpagename);

                        }

                    }

                    else

                    {

                        String search2Sql =
                                "select count(1) from ldmenu where runscript like '%" +
                                servletPathBak + "%'";

                        SSRS t2SSRS = tExeSQL.execSQL(search2Sql);

                        if (t2SSRS != null)

                        {

                            String tt2[] = t2SSRS.getRowData(1);

                            if (!tt2[0].equals("0"))

                            {

                                if (!PubFun.canIDo(tGI, ".."+servletPathBak,
                                        "menu"))

                                {
                                	System.out.println("dirPath****:" + servletPath);
                                    HttpServletResponse hres = (
                                            HttpServletResponse) response;

                                    hres.sendRedirect(indexpagename);

                                }

                            }

                        }

                    }

                }

                chain.doFilter(request, response);

            }

        }

        else {
            chain.doFilter(request, response);
        }

    }


    private boolean isExcludePage(String servletPath)

    {

        boolean excluded = false;

        if (excludePages != null)

        {

            for (int i = 0; i < excludePages.length; i++)

            {

                if (servletPath.equals(excludePages[i]))

                {

                    excluded = true;

                    break;

                }

            }

        }

        return excluded;

    }


    public void destroy()

    {

    }

}
