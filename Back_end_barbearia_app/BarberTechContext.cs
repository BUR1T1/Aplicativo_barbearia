using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;

namespace Back_end_barbearia_app
{
    public class BarberTechContext : DbContext
    {
        public BarberTechContext(DbContextOptions<BarberTechContext> options) : base(options) { }

        public DbSet<Barbeiro> Barbeiros { get; set; }
        public DbSet<Cliente> Clientes { get; set; }
        public DbSet<Servico> Servicos { get; set; }
        public DbSet<Produto> Produtos { get; set; }
        public DbSet<Agendamento> Agendamentos { get; set; }
    }
}
