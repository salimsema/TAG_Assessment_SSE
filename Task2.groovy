import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.transform.BaseScript
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

@BaseScript CustomEndpointDelegate delegate

getParentIssueKeyUpdated(httpMethod: "GET", groups: ["adaptavist-users"]) {
    MultivaluedMap queryParams, body, HttpServletRequest request ->

    try {
        //Retrieve issueKey from queryParams
        String issueKey = queryParams.getFirst('issueKey')

        //Checking if issueKey is missing in queryParams
        if (!issueKey) {
            return Response.status(400).entity("issueKey is missing in the parameter").build()
        }
		
        //Checking if issueKey provided in queryParams is valid or not
		def issueManager = ComponentAccessor.getIssueManager()
		def issue = issueManager.getIssueByCurrentKey(issueKey)

        //Throwing 404 with valid message if it is not valid
        if (!issue) {
            return Response.status(404).entity("Issue with provided issueKey is not found").build()
        }
		
        //Fetching issue using issueKey
		issue = Issues.getByKey(issueKey)

        //Accessing parent using issue. If no parent found than throwing 404 with valid message
        def parent = issue.getParentObject()
        if (!parent) {
            return Response.status(404).entity("Provided issueKey has no parents").build()
        }

        //Returning parent in response
        def parentIssueKey = parent.key
        return Response.ok(new JsonBuilder([parentKey: parentIssueKey]).toPrettyString()).build()

    } catch (Exception e) {
        // Generic error response
        return Response.serverError()
            .entity("Error occurred: ${e.message}")
            .build()
    }
}
