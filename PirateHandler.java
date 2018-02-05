package bots;

import pirates.Asteroid;
import pirates.Capsule;
import pirates.Location;
import pirates.Mothership;
import pirates.Pirate;
import pirates.PirateGame;

public class PirateHandler {

	private Pirate pirate;
	private boolean acted;
	private Role role;
	


	public PirateHandler(Pirate pirate) {
		this.pirate = pirate;
		this.acted = false;
		this.role = Role.NOT_ASSIGNED;
	}

	
	public void doTurn() {
		switch(this.role) {
		case HOLDER:
			doHolderTurn();
			break;
		case DEFENDER:
			doDefenderTurn();
			break;
		case PROTECTOR:
			doProtectorTurn();
			break;
		case CHASER:
			doChaserTurn();
			break;
		default:
			break;
		}
	}

	/**
	 * Performs a turn for the defenders role/pirate
	 */
	private void doDefenderTurn() {
		for(Asteroid asteroid : MyBot.game.getLivingAsteroids()) {
			if(this.pirate.canPush(asteroid)) {
				if(this.tryPushAsteroidToCarrier());
					return;
			}
		}
		for(Pirate enemyHolder : MyBot.enemyCapsuleHolders) {
			if(this.pirate.canPush(enemyHolder)) {
				pushPirate(enemyHolder);
				return;
			}
			if(this.pirate.getLocation().inRange(enemyHolder, this.pirate.pushDistance + this.pirate.maxSpeed)) {
				pirate.sail(enemyHolder);
				return;
			}
		}
		int minDistance = Integer.MAX_VALUE;
		MothershipHandler closestMothership = null;
		for(MothershipHandler mothership : MyBot.MOTHERSHIPS) {
			int distance = this.pirate.distance(mothership.getMothership());
			if(distance < minDistance && mothership.getDefenders() < MyBot.amountDefendersPerCity) {
				minDistance = distance;
				closestMothership = mothership;
				mothership.addDefender();
			}
		}
		if(minDistance == 0) {
			for(Pirate enemyPirate : MyBot.game.getEnemyLivingPirates()) {
				if(this.pirate.canPush(enemyPirate)) {
					pushPirate(enemyPirate);
					return;
				}
			}
		}
		if(closestMothership != null) 
			this.pirate.sail(closestMothership.getMothership());
	}

	/**
	 * Performs a turn for the chaser role/pirate
	 */
	private void doChaserTurn() {
		for(Pirate holder : MyBot.enemyCapsuleHolders) {
			if(this.pirate.canPush(holder)) {
				pushPirate(holder);
				return;
			}
		}
		Pirate enemyHolder = this.getNearestEnemyHolder();
		if(enemyHolder != null) {
			pushPirate(enemyHolder);
			return;
		}
		this.pirate.sail(this.getNearestEnemyCapsule());
		
	}
	


	/**
	 * Not done, Ret later
	 */
	private void doHolderTurn() {
		if(this.pirate.hasCapsule()) {
			this.pirate.sail(getClosestMothership());
			return;
		}
		this.pirate.sail(this.getNearestFriendlyCapsule());
	}

	
	
	/**
	 * Performs a turn for the protector role/pirate
	 */
	private void doProtectorTurn() {
		if(getNearestFriendlyCapsule().owner == null) {
			this.pirate.sail(getNearestFriendlyCapsule());
			return;
		}
		if(tryPushHolderToMothership())
			return;
		for(Pirate holder : MyBot.enemyCapsuleHolders) {
			if(this.pirate.canPush(holder)) {
				pushPirate(holder);
				return;
			}
		}
		for(Pirate enemyPirate : MyBot.game.getEnemyLivingPirates()) {
			if(this.pirate.canPush(enemyPirate)) {
				pushPirate(enemyPirate);
				return;
			}
		}
		this.pirate.sail(getNearestFriendlyHolder().pirate);
	}
	

	/**
	 * Chaser method
	 * @return the nearest enemy capsule , doesn't have to have an owner
	 */
	private Capsule getNearestEnemyCapsule() {
		Capsule closestCapsule = null;
		int minDistance = Integer.MAX_VALUE;
		for(Capsule capsule : MyBot.game.getEnemyCapsules()) {
			if(closestCapsule == null) {
				closestCapsule = capsule;
				minDistance = this.pirate.distance(capsule);
				continue;
			}
			int distance = this.pirate.distance(capsule);
			if(distance < minDistance) {
				closestCapsule = capsule;
				minDistance = distance;
			}
		}
		return closestCapsule;
	}
	
	/**
	 * Protector method
	 * Tries to push the holder to the nearest mothership if possible
	 * @return true if pushed , false otherwise
	 */
	private boolean tryPushHolderToMothership() {
		PirateHandler holder = getNearestFriendlyHolder();
		if(this.pirate.canPush(holder.pirate)) {
			Mothership mothership = holder.getClosestMothership();
			int distanceToMothership = holder.pirate.distance(mothership);
			if(distanceToMothership < this.pirate.pushDistance) {
				this.pirate.push(holder.pirate, mothership);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Protector method , might have a usage elsewhere
	 * @return the nearest friendly holder
	 */
	private PirateHandler getNearestFriendlyHolder() {
		PirateHandler nearPirate = null;
		int minDistance = Integer.MAX_VALUE;
		for(PirateHandler handler : MyBot.PIRATES) {
			if(handler.role != Role.HOLDER)
				continue;
			if(nearPirate == null) {
				nearPirate = handler;
				minDistance = this.pirate.distance(nearPirate.pirate);
				continue;
			}
			int distance = this.pirate.distance(nearPirate.pirate);
			if(distance < minDistance) {
				nearPirate = handler;
				minDistance = distance;
			}
		}
		return nearPirate;
	}
	
	/**
	 * 
	 * @return the nearest friendly mothership
	 */
	private Mothership getClosestMothership() {
		Mothership nearMothership = null;
		int minDistance = Integer.MAX_VALUE;
		for(Mothership mothership : MyBot.game.getMyMotherships()) {
			if(nearMothership == null) {
				nearMothership = mothership;
				minDistance = this.pirate.distance(nearMothership);
				continue;
			}
			int distance = this.pirate.distance(nearMothership);
			if(distance < minDistance) {
				nearMothership = mothership;
				minDistance = distance;
			}
		}
		return nearMothership;
	}




	/*
	 * Capsule(2 , 2)
	 * Defense(x - 4)
	 * 
	 * 
	 * HOLDER
	 * PROTECTOR
	 * DEFENDER
	 * CHASER
	 */

	/**
	 * 
	 * @return the nearest enemy capsule holder
	 */
	public Pirate getNearestEnemyHolder() {
		Pirate closest = null;
		int minDis = Integer.MAX_VALUE;
		for(Capsule capsule : MyBot.game.getEnemyCapsules()) {
			if(capsule.holder != null) {
				if(this.pirate.distance(capsule) < minDis) {
					closest = capsule.holder;
					minDis = this.pirate.distance(capsule);
				}
			}
		}
		return closest;
	}

	// pushes asteroid to carrier if possible
	public boolean tryPushAsteroidToCarrier() {
		if(enemyHasNoCapsules())
			return false;
		for(Asteroid asteroid : MyBot.game.getLivingAsteroids()) {
			if(this.pirate.canPush(asteroid)) {
				Pirate carrier = this.getNearestEnemyHolder();
				if(carrier == null)
					return false;
				int asteroidToCarrier = asteroid.distance(carrier); 
				int asteroidToPirate = this.pirate.distance(asteroid);
				int carrierToPirate = this.pirate.distance(carrier);
				if(! (carrierToPirate + asteroidToPirate == asteroidToCarrier)) {
					this.pirate.push(asteroid, carrier);
					return true;
				}
			}
		}
		return false;
	}


	private boolean enemyHasNoCapsules() {
		for(Pirate pirate : MyBot.game.getEnemyLivingPirates()) {
			if(pirate.hasCapsule())
				return false;
		}
		return true;
	}

	
	/**
	 *  pushes the enemy pirate to the best place.
	 * @param this.pirate
	 * @param e
	 */
	private void pushPirate(Pirate enemy){
		Location enemyLocation = enemy.getLocation();
		// right
		boolean haveToPush = false;
		if(enemy.hasCapsule()){
			for(Pirate pirate : MyBot.game.getMyLivingPirates()){
				if(pirate.id != this.pirate.id && pirate.canPush(enemy)){
					haveToPush = true;
					break;
				}
			}
		}
		Location pushRowRight = new Location(enemyLocation.row + MyBot.game.pushDistance,enemyLocation.col);
		Location pushColRight = new Location(enemyLocation.row , MyBot.game.pushDistance + enemyLocation.col);
		if(!MyBot.game.inMap(pushColRight)){
			this.pirate.push(enemy, pushColRight);
			return;
		}
		if(!MyBot.game.inMap(pushRowRight)){
			this.pirate.push(enemy, pushRowRight);
			return;
		}
		// left
		Location pushRowLeft = new Location(enemyLocation.row - MyBot.game.pushDistance,enemyLocation.col);
		Location pushColLeft = new Location(enemyLocation.row , enemyLocation.col - MyBot.game.pushDistance);
		if(!MyBot.game.inMap(pushColLeft)){
			this.pirate.push(this.pirate, pushColLeft);
			return;
		}
		if(!MyBot.game.inMap(pushRowLeft)){
			this.pirate.push(enemy, pushRowLeft);
			return;
		}

		for(Asteroid a : MyBot.game.getLivingAsteroids()){
			if(enemy.inRange(a, MyBot.game.pushDistance + MyBot.game.asteroidSize)){
				this.pirate.push(enemy, a);
				return;
			}
		}
		int closerToLimit = Integer.MAX_VALUE;
		Location pushDest = null;
		if(haveToPush){
			if((MyBot.game.rows -pushRowRight.row) >= pushRowLeft.row){
				closerToLimit = pushRowLeft.row;
				pushDest = pushRowLeft;
			}
			else{
				closerToLimit = (MyBot.game.rows -pushRowRight.row);
				pushDest = pushRowRight;
			}
			if((MyBot.game.cols - pushColRight.col) >= pushColLeft.col){
				if(pushColLeft.col< closerToLimit){
					pushDest =  pushRowLeft;
				}
			}
			else if((MyBot.game.cols - pushColRight.col) < closerToLimit){
				pushDest = pushColRight; 
			}
			if(pushDest != null){
				this.pirate.push(enemy, pushDest);
			}
		}
	}
	
	private Capsule getNearestFriendlyCapsule() {
		Capsule closestCapsule = null;
		int minDistance = Integer.MAX_VALUE;
		for(Capsule capsule : MyBot.game.getMyCapsules()) {
			if(closestCapsule == null) {
				closestCapsule = capsule;
				minDistance = this.pirate.distance(capsule);
				continue;
			}
			int distance = this.pirate.distance(capsule);
			if(distance < minDistance) {
				closestCapsule = capsule;
				minDistance = distance;
			}
		}
		return closestCapsule;
	}


	public Pirate getPirate() {
		return pirate;
	}


	public void setPirate(Pirate pirate) {
		this.pirate = pirate;
	}


	public boolean hasActed() {
		return acted;
	}


	public void setActed(boolean acted) {
		this.acted = acted;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}
	
}