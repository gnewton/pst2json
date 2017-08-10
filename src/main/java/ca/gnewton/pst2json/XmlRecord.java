package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class XmlRecord{
    private String from_name=null;
    private String from=null;
    private String received=null;
    private String subject=null;
    private String message_id=null;
    private String in_reply_to_id=null;
    private String return_path=null;
    private String transport_message_headers=null;
    private String conversation_id=null;
    /**
     * Describe foldersPath here.
     */
    private String foldersPath;

    @XmlElement(name="attachments")
    protected XmlAttachments mattachments;

    @XmlElement(name="recipients")
    protected XmlRecipients mrecipients;
    
    public final String getConversation_id() {
	return conversation_id;
    }
    @XmlElement
    public final void setConversation_id(final String conversation_id) {
	this.conversation_id = conversation_id;
    }

    String[] folder;

    @XmlElement
    public final String getSubject() {
	return subject;
    }
    public final void setSubject(final String subject) {
	this.subject = subject;
    }


    @XmlElement
    public final String getReceived() {
	return received;
    }
    public final void setReceived(final String received) {
	this.received = received;
    }

    @XmlElement
    public final String getFrom_name() {
	return from_name;
    }
    public final void setFrom_name(final String from_name) {
	this.from_name = from_name;
    }

    @XmlElement
    public final String getFrom() {
	return from;
    }
    public final void setFrom(final String from) {
	this.from = from;
    }

    @XmlElement
    public final String getMessage_id() {
	return message_id;
    }
    public final void setMessage_id(final String message_id) {
	this.message_id = message_id;
    }

    @XmlAttribute
    public final String getIn_reply_to_id() {
	return this.in_reply_to_id;
    }
    public final void setIn_reply_to_id(final String in_reply_to_id) {
	this.in_reply_to_id = in_reply_to_id;
    }

    @XmlElement
    public final String getReturn_path() {
	return return_path;
    }
    public final void setReturn_path(final String return_path) {
	this.return_path = return_path;
    }

        @XmlElement
    public final String getTransport_message_headers() {
	return transport_message_headers;
    }
    public final void setTransport_message_headers(final String transport_message_headers) {
	this.transport_message_headers = transport_message_headers;
    }


    String body;

    boolean cc_me;
    @XmlAttribute
    public void setCc_me(boolean v){
	this.cc_me = v;
    }
    public boolean getCc_me(){
	return this.cc_me;
    }
    
    boolean message_recip_me;
    @XmlAttribute
    public void setMessage_recip_me(boolean v){
	this.message_recip_me = v;
    }
    public boolean getMessage_recip_me(){
	    return this.message_recip_me;
    }
    
    boolean message_to_me;
    @XmlAttribute
    public void setMessage_to_me(boolean v){
	this.message_to_me = v;
    }
        public boolean getMessage_to_me(){
	    return this.message_to_me;
    }
    
    boolean response_requested;
    @XmlAttribute
    public void setResponse_requested(boolean v){
	this.response_requested = v;
    }
        public boolean getResponse_requested(){
	    return this.response_requested;
    }

    boolean attachments;
    @XmlAttribute
    public void setAttachments(boolean v){
	this.attachments = v;
    }
    public boolean getAttachments(){
	return this.attachments;
    }

    
    boolean forwarded;
    @XmlAttribute
    public void setForwarded(boolean v){
	this.forwarded = v;
    }
    public boolean getForwarded(){
	return this.forwarded;
    }
    
    boolean replied;
    @XmlAttribute
    public void setReplied(boolean v){
	this.replied = v;
    }
    public boolean getReplied(){
	return this.replied;
    }
    
    boolean from_me;
    @XmlAttribute
    public void setFrom_me(boolean v){
	this.from_me = v;
    }
    public boolean getFrom_me(){
	return this.from_me;
    }

    
    boolean read;
    @XmlAttribute
    public void setRead(boolean v){
	this.read = v;
    }
    public boolean getRead(){
	return this.read;
    }
    
    boolean reply_requested;
    @XmlAttribute
    public void setReply_requested(boolean v){
	this.reply_requested = v;
    }
    public boolean getReply_requested(){
	return this.reply_requested;
    }
    
    boolean resent;
    @XmlAttribute
    public void setResent(boolean v){
	this.resent = v;
    }
    public boolean getResent(){
	    return this.resent;
    }
    
    boolean submitted;
    @XmlAttribute
    public void setSubmitted(boolean v){
	this.submitted = v;
    }
    public boolean getSubmitted(){
	return this.submitted;
    }
    
    boolean unmodified;
    @XmlAttribute
    public void setUnmodified(boolean v){
	this.unmodified = v;
    }
    public boolean getUnmodified(){
	return this.unmodified;
    }
    
    boolean unsent;
    @XmlAttribute
    public void setUnsent(boolean v){
	this.unsent = v;
    }
    public boolean getUnsent(){
	return this.unsent;
    }


    ////
    int folderDepth;
    @XmlAttribute
    public void setFolderDepth(int v){
	this.folderDepth = v;
    }
    public int getFolderDepth(){
	return this.folderDepth;
    }


    int importance;
    @XmlAttribute
    public void setImportance(int v){
	this.importance = v;
    }
    public int getImportance(){
	return this.importance;
    }

    
    int internet_article_number;
    @XmlAttribute
    public void setInternet_article_number(int v){
	this.internet_article_number = v;
    }
    public int getInternet_article_number(){
	return this.internet_article_number;
    }
    
    int num_recipients;
    @XmlElement
    public void setNum_recipients(int v){
	this.num_recipients = v;
    }
    public int getNum_recipients(){
	return this.num_recipients;
    }
    
    int num_attachments;
    @XmlAttribute
    public void setNum_attachments(int v){
	this.num_attachments = v;
    }
    public int getNum_attachments(){
	    return this.num_attachments;
    }
    int priority;
    @XmlAttribute
    public void setPriority(int v){
	this.priority = v;
    }
        public int getPriority(){
	return this.priority;
    }
    
    int sensitivity;
    @XmlAttribute
    public void setSensitivity(int v){
	this.sensitivity = v;
    }
        public int getSensitivity(){
	    return this.sensitivity;
    }

    @XmlAttribute
    public final String getFoldersPath() {
	return foldersPath;
    }
    public final void setFoldersPath(final String foldersPath) {
	this.foldersPath = foldersPath;
    }

}
