  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show Feed</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Feed List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Feed</g:link></span>
        </div>
        <div class="body">
            <h1>Show Feed</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Id:</td>
                            
                            <td valign="top" class="value">${feed.id}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Name:</td>
                            
                            <td valign="top" class="value">${feed.name}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Url:</td>
                            
                            <td valign="top" class="value">${feed.url}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Author:</td>
                            
                            <td valign="top" class="value">${feed.author}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Tags:</td>
                            
                            <td valign="top" class="value">${feed.tags}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form controller="feed">
                    <input type="hidden" name="id" value="${feed?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
