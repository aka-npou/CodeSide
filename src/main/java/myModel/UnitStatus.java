package myModel;

/**
 * Created by aka_npou on 30.11.2019.
 */
public enum UnitStatus {
    Wait(0),
    GoToWeapon(1),
    GoToHP(2),
    GoToEnemy(3),
    GoFromEnemy(4),
    Dodge(5);

    public int discriminant;

    UnitStatus(int discriminant) {
        this.discriminant = discriminant;}
}
