package ca.gnewton.pst2json;

// From https://docs.microsoft.com/en-us/office/vba/outlook/concepts/forms/item-types-and-message-classes`
import java.util.Set;
import java.util.HashSet;

public class MessageClasses {
    public static Set<String> messageClassesMap;

static {
    messageClassesMap  = new HashSet<String>();
    messageClassesMap.add("ar01");
    messageClassesMap.add("ar02");

}

    /*
IPM.Activity 	Journal entries
IPM.Appointment 	Appointments
IPM.Contact 	Contacts
IPM.DistList 	Distribution lists
IPM.Document 	Documents
IPM.OLE.Class 	Exception item of a recurrence series
IPM 	Items for which the specified form cannot be found
IPM.Note 	Email messages
IPM.Note.IMC.Notification 	Reports from the Internet Mail Connect (the Exchange Server gateway to the Internet)
IPM.Note.Rules.OofTemplate.Microsoft 	Out-of-office templates
IPM.Post 	Posting notes in a folder
IPM.StickyNote 	Creating notes
IPM.Recall.Report 	Message recall reports
IPM.Outlook.Recall 	Recalling sent messages from recipient Inboxes
IPM.Remote 	Remote Mail message headers
IPM.Note.Rules.ReplyTemplate.Microsoft 	Editing rule reply templates
IPM.Report 	Reporting item status
IPM.Resend 	Resending a failed message
IPM.Schedule.Meeting.Canceled 	Meeting cancellations
IPM.Schedule.Meeting.Request 	Meeting requests
IPM.Schedule.Meeting.Resp.Neg 	Responses to decline meeting requests
IPM.Schedule.Meeting.Resp.Pos 	Responses to accept meeting requests
IPM.Schedule.Meeting.Resp.Tent 	Responses to tentatively accept meeting requests
IPM.Note.Secure 	Encrypted notes to other people
IPM.Note.Secure.Sign 	Digitally signed notes to other people
IPM.Task 	Tasks
IPM.TaskRequest.Accept 	Responses to accept task requests
IPM.TaskRequest.Decline 	Responses to desline task requests
IPM.TaskRequest 	Task requests
IPM.TaskRequest.Update 	Updates to requested tasks
    */
}
