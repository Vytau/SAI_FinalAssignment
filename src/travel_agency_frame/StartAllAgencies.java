package travel_agency_frame;

import java.awt.EventQueue;


public class StartAllAgencies {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Fast",
							"bookFastQueue");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Cheap",
							"bookCheapQueue");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BookingAgencyFrame  frame = new BookingAgencyFrame ("Book Good Service",
							"bookGoodServiceQueue");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
