package ee.lutsu.alpha.mc.modifyworld2;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;

public enum EntityCategory {
	PLAYER("player", EntityPlayer.class),
	ITEM("item", EntityItem.class),
	ANIMAL("animal", EntityAnimal.class, EntitySquid.class),
	MONSTER("monster", EntityMob.class, EntitySlime.class, EntityDragon.class, EntityGhast.class ),
	NPC("npc", EntityVillager.class),
	PROJECTILE("projectile", EntityArrow.class);
	
	private String name;
	private Class<? extends Entity> classes[];
	
	private final static Map<Class<? extends Entity>, EntityCategory> map = new HashMap<Class<? extends Entity>, EntityCategory>();
	
	static {
		for (EntityCategory cat : EntityCategory.values()) {
			for (Class<? extends Entity> catClass : cat.getClasses()) {
				map.put(catClass, cat);
			}
		}
	}
	
	private EntityCategory(String name, Class<? extends Entity>... classes) {
		this.name = name;
		this.classes = classes;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getNameDot() {
		return this.getName() + ".";
	}
	
	public Class<? extends Entity>[] getClasses() {
		return this.classes;
	}
	
	public static EntityCategory fromEntity(Entity entity) {
		for (Class<? extends Entity> entityClass : map.keySet()) {
			if (entityClass.isAssignableFrom(entity.getClass())) {
				return map.get(entityClass);
			}
		}
		
		return null;
	}
	
}
