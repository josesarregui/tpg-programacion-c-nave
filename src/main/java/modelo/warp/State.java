package modelo.warp;

public interface State {
    void prepararSalto();
    void iniciarWarp();
    void desactivarWarp();
    void enfriar();
}
