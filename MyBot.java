package bots;

import java.util.ArrayList;

import pirates.Capsule;
import pirates.Location;
import pirates.Mothership;
import pirates.Pirate;
import pirates.PirateBot;
import pirates.PirateGame;
/**
 * This is an example for a bot.
 */
public class MyBot implements PirateBot {
	
	
	public static int amountDefendersPerCity;
	public static final ArrayList<PirateHandler> PIRATES = new ArrayList<PirateHandler>();
	public static PirateGame game;
	public static int amountOfCapsules = 0;
	public static int amountOfMotherships = 0;
	public static int assigned;
	public boolean firstFlag = true;
	public ArrayList<Location> defendersLocations = new ArrayList<Location>(); 
	public static final ArrayList<MothershipHandler> MOTHERSHIPS = new ArrayList<MothershipHandler>();
	public static ArrayList<Pirate>enemyCapsuleHolders = new ArrayList<Pirate>();
	
	@Override
	public void doTurn(PirateGame game) throws Exception {
		// TODO Auto-generated method stub
		init(game);
		printEachRole();
		for(PirateHandler handler : PIRATES) {
			handler.doTurn();
		}
		
		
	}
	
	private void printEachRole() {
		for(PirateHandler handler : PIRATES) {
			if(handler.getRole() == Role.HOLDER)
				System.out.println("Holder : " + handler.getPirate());
			if(handler.getRole() == Role.CHASER)
				System.out.println("Chaser : " + handler.getPirate());
			if(handler.getRole() == Role.DEFENDER)
				System.out.println("Defender : " + handler.getPirate());
			if(handler.getRole() == Role.PROTECTOR)
				System.out.println("Protector : " + handler.getPirate());
		}
	}
	
	
	private void init(PirateGame game) {
		MyBot.game = game;
		assigned = 0;
		PIRATES.clear();
		enemyCapsuleHolders.clear();
		amountOfCapsules = game.getMyCapsules().length;
		amountOfMotherships = game.getMyMotherships().length;
		for(Pirate pirate : game.getMyLivingPirates()) 
			PIRATES.add(new PirateHandler(pirate));
		for(Mothership m : game.getEnemyMotherships()){
			MOTHERSHIPS.add(new MothershipHandler(m));
		}
		assignRoles();
		for(Pirate enemyPirate : game.getAllEnemyPirates()){
			if(enemyPirate.hasCapsule()){
				enemyCapsuleHolders.add(enemyPirate);
			}
		}
	}
	
	
	private void assignRoles() {
		assignHolders();
		assignProtectors();
		assignDefenders();
		assignChasers();
	}
	
	private void assignChasers() {
		for(PirateHandler handler : PIRATES) {
			if(handler.getRole() == Role.NOT_ASSIGNED)
				handler.setRole(Role.CHASER);
		}
	}
	
	
	// defenders - 
	
	

	
	
	private void assignDefenders() {
		// make it so that the closest pirates to the mothership will be the defenders.
		int amountOfDefenders = game.getAllMyPirates().length - assigned;
		amountDefendersPerCity = amountOfDefenders/amountOfMotherships;
		
		int counter;
		for(Mothership m : game.getMyMotherships()) {
			counter = 0;
			for(PirateHandler pirate :PIRATES) {
				if(pirate.getRole() == Role.NOT_ASSIGNED) {
					pirate.setRole(Role.DEFENDER);
					counter++;
				}
				if(counter == amountDefendersPerCity)
					break;
			}
		}
	}
	
	private void assignProtectors() {
		for(PirateHandler handler : PIRATES) {
			if(handler.getRole() != Role.HOLDER)
				continue;
			PirateHandler nearPirate = null;
			int minDistance = Integer.MAX_VALUE;
			for(PirateHandler pirate : PIRATES) {
				if(pirate.getRole() == Role.HOLDER)
					continue;
				if(pirate.getPirate().distance(handler.getPirate()) < minDistance ) {
					nearPirate = pirate;
					minDistance = pirate.getPirate().distance(handler.getPirate());
				}
			}
			if(nearPirate!=null) {
				nearPirate.setRole(Role.PROTECTOR);
				assigned++;
			}
		}
	}
	
	private void assignHolders() {
		for(Capsule capsule : game.getMyCapsules()) {
			PirateHandler handler = getNearestPirateToCapsule(capsule);
			handler.setRole(Role.HOLDER);
			assigned++;
		}
	}
	
	
	
	
	private PirateHandler getNearestPirateToCapsule(Capsule capsule) {
		int minDistance = Integer.MAX_VALUE;
		PirateHandler nearPirate = null;
		for(PirateHandler pirate : PIRATES) {
			if(nearPirate == null) {
				nearPirate = pirate;
				minDistance = nearPirate.getPirate().distance(capsule);
			} else if(pirate.getPirate().distance(capsule) < minDistance) {
				nearPirate = pirate;
				minDistance = pirate.getPirate().distance(capsule);
			}
		}
		return nearPirate;
	}
	
	

	


} // end of class


	
