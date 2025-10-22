using System;

namespace Back_end_barbearia_app.Models
{
    public class Servico
{
    public int Id { get; set; }
    public string Nome { get; set; }
    public double Preco { get; set; }
    public int DuracaoMin { get; set; }
}
}