using System;

namespace Back_end_barbearia_app.Models
{
    public class Agendamento
{
    public int Id { get; set; }
    public int ClienteId { get; set; }
    public int BarbeiroId { get; set; }
    public int ServicoId { get; set; }
    public DateTime DataHora { get; set; }
    public string Status { get; set; } = "pendente"; // "pendente", "concluido", "cancelado"
}
}