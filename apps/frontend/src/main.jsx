import React from 'react';
import ReactDOM from 'react-dom/client';
import { BriefcaseBusiness, Code, Send, ShieldCheck, Terminal } from 'lucide-react';
import { api } from './api';
import './styles.css';

const emptyProject = {
  title: '',
  description: '',
  technologiesText: '',
  githubUrl: '',
  demoUrl: '',
  featured: false,
};

function App() {
  return window.location.pathname.startsWith('/admin') ? <AdminPage /> : <HomePage />;
}

function HomePage() {
  const [portfolio, setPortfolio] = React.useState(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [comment, setComment] = React.useState({ authorName: '', authorEmail: '', content: '' });

  React.useEffect(() => {
    api.portfolio().then(setPortfolio).catch((err) => setError(err.message));
  }, []);

  async function submit(event) {
    event.preventDefault();
    setMessage('');
    try {
      await api.comment(comment);
      setComment({ authorName: '', authorEmail: '', content: '' });
      setMessage('Commentaire envoye. Il sera visible apres validation admin.');
    } catch (err) {
      setMessage(err.message);
    }
  }

  if (error) return <Status title="API indisponible" message={error} />;
  if (!portfolio) return <Status title="Chargement" message="Connexion a l'API Spring Boot..." />;

  const { profile, projects, comments } = portfolio;

  return (
    <main>
      <section className="hero">
        <div className="hero-content">
          <p className="eyebrow"><Terminal size={18} /> DevOps Portfolio Platform</p>
          <h1>{profile.fullName}</h1>
          <h2>{profile.headline}</h2>
          <p>{profile.bio}</p>
          <div className="actions">
            <a href={profile.githubUrl} target="_blank" rel="noreferrer"><Code size={18} /> GitHub</a>
            <a href={profile.linkedinUrl} target="_blank" rel="noreferrer"><BriefcaseBusiness size={18} /> LinkedIn</a>
          </div>
        </div>
      </section>

      <section className="section">
        <p className="eyebrow">Projects</p>
        <h2>Projets principaux</h2>
        <div className="grid">
          {projects.map((project) => (
            <article className="card" key={project.id}>
              <div className="card-top">
                <h3>{project.title}</h3>
                {project.featured && <span>Featured</span>}
              </div>
              <p>{project.description}</p>
              <div className="tags">{project.technologies.map((tech) => <span key={tech}>{tech}</span>)}</div>
            </article>
          ))}
        </div>
      </section>

      <section className="section comments-layout">
        <div>
          <p className="eyebrow">Comments</p>
          <h2>Commentaires approuves</h2>
          <div className="comments-list">
            {comments.length === 0 && <p>Aucun commentaire approuve pour le moment.</p>}
            {comments.map((item) => <article className="comment" key={item.id}><strong>{item.authorName}</strong><p>{item.content}</p></article>)}
          </div>
        </div>
        <form className="panel" onSubmit={submit}>
          <h3>Laisser un commentaire</h3>
          <input placeholder="Nom" value={comment.authorName} onChange={(e) => setComment({ ...comment, authorName: e.target.value })} />
          <input placeholder="Email" value={comment.authorEmail} onChange={(e) => setComment({ ...comment, authorEmail: e.target.value })} />
          <textarea placeholder="Message" value={comment.content} onChange={(e) => setComment({ ...comment, content: e.target.value })} />
          <button type="submit"><Send size={18} /> Envoyer</button>
          {message && <p className="form-message">{message}</p>}
        </form>
      </section>
    </main>
  );
}

function AdminPage() {
  const [token, setToken] = React.useState(localStorage.getItem('adminToken') || '');
  const [credentials, setCredentials] = React.useState({ username: 'admin', password: '' });
  const [activeTab, setActiveTab] = React.useState('profile');
  const [profile, setProfile] = React.useState(null);
  const [projects, setProjects] = React.useState([]);
  const [comments, setComments] = React.useState([]);
  const [projectForm, setProjectForm] = React.useState(emptyProject);
  const [editingProjectId, setEditingProjectId] = React.useState(null);
  const [error, setError] = React.useState('');
  const [message, setMessage] = React.useState('');

  React.useEffect(() => {
    if (token) loadAdminData(token);
  }, [token]);

  async function loadAdminData(currentToken = token) {
    setError('');
    try {
      const [profileData, projectData, commentData] = await Promise.all([
        api.adminProfile(currentToken),
        api.projects(),
        api.adminComments(currentToken),
      ]);
      setProfile(profileData);
      setProjects(projectData);
      setComments(commentData);
    } catch (err) {
      setError(err.message);
    }
  }

  async function signIn(event) {
    event.preventDefault();
    setError('');
    try {
      const response = await api.login(credentials);
      localStorage.setItem('adminToken', response.token);
      setToken(response.token);
    } catch (err) {
      setError(err.message);
    }
  }

  function signOut() {
    localStorage.removeItem('adminToken');
    setToken('');
    setProfile(null);
    setProjects([]);
    setComments([]);
  }

  async function saveProfile(event) {
    event.preventDefault();
    setMessage('');
    try {
      const updated = await api.updateProfile(token, profile);
      setProfile(updated);
      setMessage('Profil mis a jour.');
    } catch (err) {
      setError(err.message);
    }
  }

  function editProject(project) {
    setEditingProjectId(project.id);
    setProjectForm({
      title: project.title,
      description: project.description,
      technologiesText: project.technologies.join(', '),
      githubUrl: project.githubUrl || '',
      demoUrl: project.demoUrl || '',
      featured: project.featured,
    });
    setActiveTab('projects');
  }

  function resetProjectForm() {
    setEditingProjectId(null);
    setProjectForm(emptyProject);
  }

  function projectPayload() {
    return {
      title: projectForm.title,
      description: projectForm.description,
      technologies: projectForm.technologiesText.split(',').map((item) => item.trim()).filter(Boolean),
      githubUrl: projectForm.githubUrl,
      demoUrl: projectForm.demoUrl,
      featured: projectForm.featured,
    };
  }

  async function saveProject(event) {
    event.preventDefault();
    setMessage('');
    try {
      if (editingProjectId) {
        await api.updateProject(token, editingProjectId, projectPayload());
        setMessage('Projet mis a jour.');
      } else {
        await api.createProject(token, projectPayload());
        setMessage('Projet ajoute.');
      }
      resetProjectForm();
      await loadAdminData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeProject(id) {
    setMessage('');
    try {
      await api.deleteProject(token, id);
      setMessage('Projet supprime.');
      await loadAdminData();
    } catch (err) {
      setError(err.message);
    }
  }

  async function moderate(id, action) {
    setMessage('');
    try {
      await api.moderate(token, id, action);
      setMessage('Commentaire mis a jour.');
      await loadAdminData();
    } catch (err) {
      setError(err.message);
    }
  }

  if (!token) {
    return (
      <main className="admin-page">
        <form className="login-panel" onSubmit={signIn}>
          <p className="eyebrow"><ShieldCheck size={18} /> Admin</p>
          <h1>Connexion admin</h1>
          <input placeholder="Username" value={credentials.username} onChange={(e) => setCredentials({ ...credentials, username: e.target.value })} />
          <input type="password" placeholder="Password" value={credentials.password} onChange={(e) => setCredentials({ ...credentials, password: e.target.value })} />
          <button type="submit">Se connecter</button>
          {error && <p className="form-message error">{error}</p>}
        </form>
      </main>
    );
  }

  return (
    <main className="admin-page">
      <section className="admin-shell wide">
        <div className="admin-header">
          <div><p className="eyebrow">Dashboard</p><h1>Panel admin</h1></div>
          <button onClick={signOut}>Logout</button>
        </div>

        <div className="admin-tabs">
          <button className={activeTab === 'profile' ? 'active' : ''} onClick={() => setActiveTab('profile')}>Profil</button>
          <button className={activeTab === 'projects' ? 'active' : ''} onClick={() => setActiveTab('projects')}>Projets</button>
          <button className={activeTab === 'comments' ? 'active' : ''} onClick={() => setActiveTab('comments')}>Commentaires</button>
        </div>

        {error && <p className="form-message error">{error}</p>}
        {message && <p className="form-message">{message}</p>}

        {activeTab === 'profile' && profile && (
          <form className="admin-form" onSubmit={saveProfile}>
            <h2>Modifier le contenu principal</h2>
            <input placeholder="Nom complet" value={profile.fullName} onChange={(e) => setProfile({ ...profile, fullName: e.target.value })} />
            <input placeholder="Titre" value={profile.headline} onChange={(e) => setProfile({ ...profile, headline: e.target.value })} />
            <textarea placeholder="Bio" value={profile.bio} onChange={(e) => setProfile({ ...profile, bio: e.target.value })} />
            <input placeholder="Email" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })} />
            <input placeholder="GitHub URL" value={profile.githubUrl || ''} onChange={(e) => setProfile({ ...profile, githubUrl: e.target.value })} />
            <input placeholder="LinkedIn URL" value={profile.linkedinUrl || ''} onChange={(e) => setProfile({ ...profile, linkedinUrl: e.target.value })} />
            <button type="submit">Sauvegarder le profil</button>
          </form>
        )}

        {activeTab === 'projects' && (
          <div className="admin-grid">
            <form className="admin-form" onSubmit={saveProject}>
              <h2>{editingProjectId ? 'Modifier le projet' : 'Ajouter un projet'}</h2>
              <input placeholder="Titre du projet" value={projectForm.title} onChange={(e) => setProjectForm({ ...projectForm, title: e.target.value })} />
              <textarea placeholder="Description" value={projectForm.description} onChange={(e) => setProjectForm({ ...projectForm, description: e.target.value })} />
              <input placeholder="Technologies separees par des virgules" value={projectForm.technologiesText} onChange={(e) => setProjectForm({ ...projectForm, technologiesText: e.target.value })} />
              <input placeholder="GitHub URL" value={projectForm.githubUrl} onChange={(e) => setProjectForm({ ...projectForm, githubUrl: e.target.value })} />
              <input placeholder="Demo URL" value={projectForm.demoUrl} onChange={(e) => setProjectForm({ ...projectForm, demoUrl: e.target.value })} />
              <label className="check-row"><input type="checkbox" checked={projectForm.featured} onChange={(e) => setProjectForm({ ...projectForm, featured: e.target.checked })} /> Featured</label>
              <div className="actions"><button type="submit">{editingProjectId ? 'Mettre a jour' : 'Ajouter'}</button>{editingProjectId && <button type="button" onClick={resetProjectForm}>Annuler</button>}</div>
            </form>

            <div className="admin-list">
              {projects.map((project) => (
                <article className="admin-item" key={project.id}>
                  <div>
                    <strong>{project.title}</strong>
                    {project.featured && <span>Featured</span>}
                    <p>{project.description}</p>
                  </div>
                  <div className="actions"><button onClick={() => editProject(project)}>Editer</button><button onClick={() => removeProject(project.id)}>Supprimer</button></div>
                </article>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'comments' && (
          <div className="admin-list">
            {comments.map((item) => (
              <article className="admin-item" key={item.id}>
                <div><strong>{item.authorName}</strong><span>{item.status}</span><p>{item.content}</p></div>
                <div className="actions"><button onClick={() => moderate(item.id, 'approve')}>Approve</button><button onClick={() => moderate(item.id, 'reject')}>Reject</button></div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

function Status({ title, message }) {
  return <main className="status-page"><h1>{title}</h1><p>{message}</p></main>;
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
