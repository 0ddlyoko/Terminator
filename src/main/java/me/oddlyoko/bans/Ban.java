package me.oddlyoko.bans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ban {

	private String staff_name;
	private String player_name;
	private String player_uuid;
	private String reason;
	private Date date;
	private Date exp_date;
	private int ID;
	private int cache_avancement;
	public Ban(String player_name ,String player_uuid  , int ID) {
		this.staff_name = "§7Terminator";
		this.player_name = player_name;
		this.player_uuid = player_uuid;
		this.date = new Date();
	
	}
	
		public String getDurationMessage() {
			String message = "";
			 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			 System.out.println(formatter.format(date));
			System.out.println(date.getDay());
			System.out.println(exp_date.getDay());
			if(!(date.getSeconds() - exp_date.getSeconds() == 0)) {
				int duration = 0;
				if(exp_date.getSeconds()+date.getSeconds()>60) {
					 duration = 60+exp_date.getSeconds()-date.getSeconds();
				}else {
					 duration = exp_date.getSeconds()-date.getSeconds();
				}
				
				message = duration + " secondes";
				return message;	
			}
			if(!(date.getMinutes() - exp_date.getMinutes() == 0)) {
				
				int duration = 0;
				if(exp_date.getMinutes()+date.getMinutes()>60) {
					 duration = 60+exp_date.getMinutes()-date.getMinutes();
				}else {
					 duration = exp_date.getMinutes()-date.getMinutes();
				}
				
				message = duration + " minutes";
				return message;
			}
			if(!(date.getDay()- exp_date.getDay() == 0)) {
				int duration = 0;
				if(exp_date.getDay()+date.getDay()>30) {
					 duration = 30+exp_date.getDay()-date.getDay();
				}else {
					 duration = exp_date.getDay()-date.getDay();
				}
				message = duration + " jours";
				return message;
			}
			if(!(date.getHours()- exp_date.getHours() == 0)) {
				int duration = 0;
				if(exp_date.getHours()+date.getHours()>24) {
					 duration = 24+exp_date.getHours()-date.getHours();
				}else {
					 duration = exp_date.getHours()-date.getHours();
				}
				message = duration + " heures";
				return message;
			}
			return message;
		}
	
		public String getStringDate() {
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return df.format(date);
	}
		public String getStringExpDate() {
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				return df.format(exp_date);
			}
	public String getStaff_name() {
		return staff_name;
	}
	public void setStaff_name(String staff_name) {
		this.staff_name = staff_name;
	}
	public String getPlayer_name() {
		return player_name;
	}
	public void setPlayer_name(String player_name) {
		this.player_name = player_name;
	}
	public String getPlayer_uuid() {
		return player_uuid;
	}
	public void setPlayer_uuid(String player_uuid) {
		this.player_uuid = player_uuid;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getExp_date() {
		return exp_date;
	}
	public void setExp_date(Date exp_date) {
		this.exp_date = exp_date;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}

	public int getCache_avancement() {
		return cache_avancement;
	}

	public void setCache_avancement(int cache_avancement) {
		this.cache_avancement = cache_avancement;
	}
	
}
