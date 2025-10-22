using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Back_end_barbearia_app.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Back_end_barbearia_app.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AgendamentoController : ControllerBase
    {
        private readonly BarberTechContext _context;

        public AgendamentoController(BarberTechContext context)
        {
            _context = context;
        }

        // GET: api/agendamento
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Agendamento>>> GetAll() =>
            await _context.Agendamentos.ToListAsync();

        // GET: api/agendamento/{id}
        [HttpGet("{id}")]
        public async Task<ActionResult<Agendamento>> GetById(int id)
        {
            var agendamento = await _context.Agendamentos.FindAsync(id);
            if (agendamento == null) return NotFound();
            return agendamento;
        }

        // POST: api/agendamento
        [HttpPost]
        public async Task<ActionResult<Agendamento>> Create(Agendamento agendamento)
        {
            _context.Agendamentos.Add(agendamento);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = agendamento.Id }, agendamento);
        }

        // PUT: api/agendamento/{id}
        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, Agendamento agendamento)
        {
            if (id != agendamento.Id) return BadRequest();

            _context.Entry(agendamento).State = EntityState.Modified;
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // PUT: api/agendamento/{id}/status
        [HttpPut("{id}/status")]
        public async Task<IActionResult> UpdateStatus(int id, [FromBody] string novoStatus)
        {
            var agendamento = await _context.Agendamentos.FindAsync(id);
            if (agendamento == null) return NotFound();

            agendamento.Status = novoStatus.ToLower();
            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/agendamento/{id}
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var agendamento = await _context.Agendamentos.FindAsync(id);
            if (agendamento == null) return NotFound();

            _context.Agendamentos.Remove(agendamento);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
