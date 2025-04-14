import com.atlassian.jira.component.ComponentAccessor

//Get logged in user from ComponentAccessor
def user = ComponentAccessor.jiraAuthenticationContext.loggedInUser
def groupManager = ComponentAccessor.groupManager

//Allow only user from jira-administrators to access the script.
if (!groupManager.isUserInGroup(user, "jira-administrators")) {
    log.debug("User is not in jira-administrators group")
    return
}

// Get all subtasks below an issue
def jqlQuery = "parent=${issue.key}"

// Specify the name below for the global Done transition
def doneTransitionName = "Done"

// Get all subtask issues for the current issue
def allSubtasks = Issues.search(jqlQuery)

// Iterate over each subtask returned
allSubtasks.each { subtask ->
    subtask.transition(doneTransitionName)
}

//Mark parent transition also DONE
issue.transition(doneTransitionName)
