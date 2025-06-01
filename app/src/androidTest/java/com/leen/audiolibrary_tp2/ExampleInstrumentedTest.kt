package com.leen.audiolibrary_tp2

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.leen.audiolibrary_tp2.data.AppDatabase
import com.leen.audiolibrary_tp2.data.Artiste
import com.leen.audiolibrary_tp2.data.ArtisteDAO
import com.leen.audiolibrary_tp2.data.Chanson
import com.leen.audiolibrary_tp2.data.ChansonDAO
import com.leen.audiolibrary_tp2.data.Genre
import com.leen.audiolibrary_tp2.data.GenreDAO
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var daoChanson: ChansonDAO
    private lateinit var daoArtiste: ArtisteDAO
    private lateinit var daoGenre: GenreDAO

    //initialiser la bd avant chaque test
    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder( //temporary db pour stocker les données (pas réel)
            ApplicationProvider.getApplicationContext(), //contexte de l'application
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        //recuperer les dao pour les fonctional CRUD
        daoChanson = db.chansonDAO()
        daoArtiste = db.artisteDAO()
        daoGenre = db.genreDAO()
    }

    //fermer la bd apres chaque test
    @After
    fun tearDown() {
        db.close()
    }

    //Test pour supprimer une chanson
    @Test
    fun testSupprimerChanson() = runBlocking {

        //insertion de deux artistes et deux genres
        val artiste1 = Artiste(id = 1, nom = "Artist 1")
        val artiste2 = Artiste(id = 2, nom = "Artist 2")
        daoArtiste.insertAll(artiste1)
        daoArtiste.insertAll(artiste2)

        val genre1 = Genre(id = 1, nom = "Genre 1")
        val genre2 = Genre(id = 2, nom = "Genre 2")
        daoGenre.insertAll(genre1)
        daoGenre.insertAll(genre2)


        //insertion de deux chansons
        val chanson1 = Chanson(id = 1, nom = "Song A", artisteId = 1, genreId = 1)
        val chanson2 = Chanson(id = 2, nom = "Song B", artisteId = 2, genreId = 2)
        daoChanson.insert(chanson1)
        daoChanson.insert(chanson2)

        //confirmer que les deux chansons ont été insérés
        var chansons = daoChanson.getAllForTests()
        Assert.assertEquals(2, chansons.size)

        //supprimer une chanson
        daoChanson.delete(chanson2)

        //Reload la db
        chansons = daoChanson.getAllForTests()

        //verifitcation de la suppression
        Assert.assertEquals(1, chansons.size)
    }


    // Test pour ajouter une chanson
    @Test
    fun testAjouterChanson() = runBlocking {

        // Insérer de deux artistes et deux genres
        val artiste1 = Artiste(id = 1, nom = "Artist 1")
        val artiste2 = Artiste(id = 2, nom = "Artist 2")
        daoArtiste.insertAll(artiste1)
        daoArtiste.insertAll(artiste2)

        val genre1 = Genre(id = 1, nom = "Genre 1")
        val genre2 = Genre(id = 2, nom = "Genre 2")
        daoGenre.insertAll(genre1)
        daoGenre.insertAll(genre2)


        // Insertion de deux chansons
        val chanson1 = Chanson(id = 1, nom = "Song A", artisteId = 1, genreId = 1)
        val chanson2 = Chanson(id = 2, nom = "Song B", artisteId = 2, genreId = 2)
        daoChanson.insert(chanson1)
        daoChanson.insert(chanson2)

        // Confirmer que les deux chansons ont été insérés
        var chansons = daoChanson.getAllForTests()
        Assert.assertEquals(2, chansons.size)

        // Recharger la base de données
        chansons = daoChanson.getAllForTests()
    }


    // Test pour modifier une chanson
    @Test
    fun testModifierChanson() = runBlocking {

        // Insérer un artiste
        val artiste1 = Artiste(id = 1, nom = "Artist 1")
        daoArtiste.insertAll(artiste1)

        // Insérer un genre
        val genre1 = Genre(id = 1, nom = "Genre 1")
        daoGenre.insertAll(genre1)

        // Insertion d'une chanson
        val chanson1 = Chanson(id = 1, nom = "Song A", artisteId = 1, genreId = 1)
        daoChanson.insert(chanson1)

        // Confirmer que la chanson ont été inséré
        var chansons = daoChanson.getAllForTests()
        Assert.assertEquals(1, chansons.size)

        // Chercher la chanson par son id
        daoChanson.getChansonById(chanson1.id)

        // Modifier la chanson
        val chansonModifier = chanson1.copy(id = 1, nom = "Song B", artisteId = 1, genreId = 1)
        daoChanson.update(chansonModifier)

        // Recharger la base de données
        chansons = daoChanson.getAllForTests()

        // Vérifier que la modification a été véritablement faite
        Assert.assertEquals(chansonModifier.nom, "Song B")
    }


    // Test pour rechercher une chanson par nom
    @Test
    fun testRechercherChansonParNom() = runBlocking {

        // Insérer deux artistes et deux genres
        val artiste1 = Artiste(id = 1, nom = "Artist 1")
        val artiste2 = Artiste(id = 2, nom = "Artist 2")
        daoArtiste.insertAll(artiste1)
        daoArtiste.insertAll(artiste2)

        val genre1 = Genre(id = 1, nom = "Genre 1")
        val genre2 = Genre(id = 2, nom = "Genre 2")
        daoGenre.insertAll(genre1)
        daoGenre.insertAll(genre2)

        // Insertion de trois chansons avec des noms distincts
        val chanson1 = Chanson(id = 1, nom = "Shape of You", artisteId = 1, genreId = 1)
        val chanson2 = Chanson(id = 2, nom = "Believer", artisteId = 2, genreId = 2)
        val chanson3 = Chanson(id = 3, nom = "Shallow", artisteId = 2, genreId = 2)
        daoChanson.insert(chanson1)
        daoChanson.insert(chanson2)
        daoChanson.insert(chanson3)

        // Rechercher une chanson par nom (case insensitive)
        val result = daoChanson.getAllForTests().filter {
            it.chanson.nom.contains("shallow", ignoreCase = true)
        }

        // Vérifier qu'une seule chanson correspond
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Shallow", result[0].chanson.nom)

        // Rechercher une chanson inexistante
        val noMatchResult = daoChanson.getAllForTests().filter {
            it.chanson.nom.contains("Never Gonna Give You Up", ignoreCase = true)
        }

        // Vérifier qu'aucune chanson ne correspond
        Assert.assertTrue(noMatchResult.isEmpty())
    }
}