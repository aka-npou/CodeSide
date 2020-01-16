package model;

public class LootBoxF {
    public Vec2Float position;
    public Vec2Float getPosition() { return position; }
    public void setPosition(Vec2Float position) { this.position = position; }
    public Vec2Float size;
    public Vec2Float getSize() { return size; }
    public void setSize(Vec2Float size) { this.size = size; }
    public Item item;
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public LootBoxF() {}
    /*public LootBoxF(Vec2Double position, Vec2Double size, Item item) {
        this.position = position;
        this.size = size;
        this.item = item;
    }*/
    public static LootBoxF readFrom(java.io.InputStream stream) throws java.io.IOException {
        LootBoxF result = new LootBoxF();
        result.position = Vec2Double.readFromF(stream);
        result.size = Vec2Double.readFromF(stream);
        result.item = Item.readFrom(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        position.writeTo(stream);
        size.writeTo(stream);
        item.writeTo(stream);
    }
}
