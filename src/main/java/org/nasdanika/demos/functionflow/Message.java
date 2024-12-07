package org.nasdanika.demos.functionflow;

import java.util.Date;

public record Message(
String sender,
String recipient,
String channel,
String text, 
String thread, 
Date time, 
Message inResponseTo,
Message relatedTo) {}